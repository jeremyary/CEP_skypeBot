package jary.rules

import groovy.util.logging.Slf4j
import org.drools.runtime.StatefulKnowledgeSession

import java.util.concurrent.atomic.AtomicBoolean

/***
 * delegate responsible for the creation, maintenance, and clean-up of a stateful rule session
 *
 * @author jary
 * @since 12/12/2012
 */
@Slf4j
class SessionLifecycleDelegate implements Runnable {

    /** rule session whose life we now toy with like a marionette */
    StatefulKnowledgeSession session

    /**
     * The container doesn't seem to want to wait for the session to clean up, so this guy is set to true
     * while the session is actively running. Once it has officially halted, it will be set to false so cleanup
     * can commence.
     */
    AtomicBoolean sessionRunning = new AtomicBoolean(false)

    /**
     * default constructor for testing
     */
    SessionLifecycleDelegate() {}

    /**
     * Constructor relating delegate to its rule session
     *
     * @param session that we will control
     */
    SessionLifecycleDelegate(StatefulKnowledgeSession session) {
        this.session = session
    }

    /**
     * create a thread for out stateful session to live on. Since we're using CEP, the session never really halts
     * unless it's a manual call, and the session is thread-blocking, so let's give it somewhere to frolick
     *
     * @param session rule session to be spun up on the new thread
     * @return handle to the session's running thread
     */
    void run() {
        log.info "Starting CEP Session"
        sessionRunning.set(true)
        session.fireUntilHalt()
        sessionRunning.set(false)
        log.info "CEP Session has been halted"
    }

    void pause() {
        session.halt()
    }

    void stop() {
        log.info "Shutting down SessionLifecycleDelegate"
        sleep (3000) //allow some time for things to finish up in session
        session.halt()
        session.dispose()
        // Need to wait until the session has completely cleaned itself up and then we can truly stop.
        while (sessionRunning.get()) {
            log.info "Session still running, waiting for it to clean up properly."
            sleep(500)
        }
        log.info "Session halted and disposed"
    }
}