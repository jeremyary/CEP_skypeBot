package jary.rules.listeners

import groovy.util.logging.Slf4j
import jary.rules.SessionManager
import org.drools.event.rule.ObjectInsertedEvent
import org.drools.event.rule.ObjectRetractedEvent
import org.drools.event.rule.ObjectUpdatedEvent
import org.drools.event.rule.WorkingMemoryEventListener
import org.drools.runtime.rule.FactHandle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/***
 * listener allowing retraction of facts from working memory based on temporal properties
 * or information found within the fact
 *
 * @author jary
 * @since 01/09/2013
 */
@Slf4j
@Component
class RetractionListener implements WorkingMemoryEventListener, Runnable {

    /** allow a sorted map to track dates and objects to be retracted */
    NavigableMap<Date, Object> retractionSchedule = new TreeMap<Date, Object>();

    /** allow some condition to signify when we have a new retractable fact to consider */
    Condition change

    /** session wrapper */
    @Autowired(required = true)
    SessionManager sessionManager

    /** tracking map for pulling fact handles by object (necessary for delayed retractions) */
    Map<Object, FactHandle> factHandleMap = [:]

    /**
     * thread lock for timer retraction...
     * we need to be able to set up a wait condition until new retractable facts are added, and then
     * either wait until that retraction deadline is reached or until a new object is inserted and we
     * need to reconsider what our "next" object to retract/deadline is.
     */
    private Lock mutex = new ReentrantLock()

    /** condition that will allow us to reset the await deadline when new objects are added */
    private Condition change = mutex.newCondition()

    /**
     * runnable task entry point for executor, sets us up for our thread manipulation
     */
    void run() {

        // LOCK 'ER DOWN CAP'N
        mutex.lock()

        try {

            // infinite loop insuring that we can remain in a wait state when needed
            while (true) {

                // we're not going to have any facts when we instantiate the listener or remove them all,
                // so we need to kick into a wait state until something is received
                while (retractionSchedule.isEmpty()) {
                    log.debug("issuing empty await")
                    change.await()
                }

                // if we're still awaiting the most-recent retraction timestamp, then we need to await until then
                while (sessionManager?.session?.sessionClock.currentTime < retractionSchedule.firstKey().time) {
                    change.awaitUntil(retractionSchedule.firstKey())
                }

                // we've reached removal point, go ahead and pull it
                log.debug("attempting to retract ${retractionSchedule.firstEntry().value}")
                FactHandle handle = factHandleMap.get(retractionSchedule.firstEntry().value)
                sessionManager.session.retract(handle)

                // go back into await state
                log.debug("issuing infinite await")
                change.await()
            }

        } catch (InterruptedException e) {
            log.error("RetractionListener thread ${Thread.currentThread().name} interrupted!")
            throw e
        } finally {
            mutex.unlock()
        }
    }

    /**
     * detect new facts, determine if they are retractable, and add to the tracker map if needed
     *
     * @param event
     */
    void objectInserted(ObjectInsertedEvent event) {

        // lock it up so that we aren't violating monitor state
        mutex.lock()

        // if we've inserted a fact from rule consequence, we need to catch up our map
        if (!factHandleMap.containsValue(event.object)) {
            factHandleMap.put(event.object, sessionManager.session.getFactHandle(event.object))
        }

        try {

            // determine if this is an object that we care to track for retraction
            if (event.object.isRetractable) {

                log.debug("retractable object of type ${event.object.class.simpleName} detected")
                long duration = event.object.duration
                if (!duration) {
                    // go ahead and throw up a similar exception to missing property for a missing value
                    throw new MissingPropertyException("no value specified for retractable object's duration")
                } else {

                    // we need to do some math from session clock + duration to determine the
                    // fact's retraction timestamp
                    Calendar calendar = new GregorianCalendar()
                    calendar.setTime(new Date(sessionManager.session.sessionClock.currentTime))
                    log.debug("duration of object noted to be ${duration} milliseconds")
                    log.debug("current time: ${calendar.time} (${calendar.time.time})")

                    calendar.add(Calendar.MILLISECOND, duration.toInteger())

                    log.debug("setting schedule for ${calendar.time} (${calendar.time.time})")
                    retractionSchedule.put(calendar.time, event.object)
                }
            }

            log.debug("signaling change condition")
            change.signal()

        } catch (Exception e) {
            if (e instanceof MissingPropertyException) {
                log.error("retractable object ${event.object} missing needed property/value 'duration'")
            }
            throw e
        } finally {
            mutex.unlock()
        }
    }

    /**
     * detect when a fact has been removed, and if necessary, remove from our tracker map
     *
     * @param event
     */
    void objectRetracted(ObjectRetractedEvent event) {

        // can't forget to handle our map whenever an object is retracted
        mutex.lock()

        try {
            if (retractionSchedule.containsValue(event.oldObject)) {
                Iterator iterator = retractionSchedule.entrySet().iterator()
                while (iterator.hasNext()) {
                    if (iterator.next().value == event.oldObject) {
                        iterator.remove()
                    }
                }
            }
            change.signal()

        } catch (InterruptedException e) {
            throw e
        } finally {
            mutex.unlock()
        }
    }

    /**
     * not needed for tracking yet, but necessary for interface
     *
     * @param event
     */
    void objectUpdated(ObjectUpdatedEvent event) {
    }
}