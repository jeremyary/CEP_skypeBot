package jary.rules.listeners

import groovy.util.logging.Slf4j
import org.drools.event.rule.ObjectInsertedEvent
import org.drools.event.rule.ObjectRetractedEvent
import org.drools.event.rule.ObjectUpdatedEvent
import org.drools.event.rule.WorkingMemoryEventListener

/***
 * full-output debugging event listener for rule session working memory (facts)
 *
 * so what's working memory? Well...
 * Whenever we insert, modify, or retract a fact, we're adding, changing, or removing the instance
 * within the working memory. You can think of working memory as the knowledge container; a place
 * where all our facts reside.
 *
 * @author jary
 * @since 12/16/2012
 */
@Slf4j
class DebugWorkingMemoryEventListener implements WorkingMemoryEventListener {

    /**
     * callback handler for when object has been placed into working memory
     *
     * @param event
     */
    void objectInserted(ObjectInsertedEvent event) {
        log.info("FACT INSERTED [${event.object.class.simpleName}]"/*: ${event}*/)
    }

    /**
     * callback handler for when object has been removed from working memory
     *
     * @param event
     */
    void objectRetracted(ObjectRetractedEvent event) {
        log.info("FACT RETRACTED [${event.oldObject.class.simpleName}]"/*: ${event}*/)
    }

    /**
     * callback handler for when object has been updated within working memory
     *
     * @param event
     */
    void objectUpdated(ObjectUpdatedEvent event) {
        log.info("FACT UPDATED [${event.object.class.simpleName}]"/*: ${event}*/)
    }
}