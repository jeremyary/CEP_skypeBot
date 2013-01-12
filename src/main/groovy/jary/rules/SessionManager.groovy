package jary.rules
import groovy.util.logging.Slf4j
import jary.rules.exceptions.MissingEntryPointException
import jary.rules.factory.RuleSessionFactory
import jary.rules.listeners.RetractionListener
import jary.rules.loader.ClasspathRuleLoader
import org.drools.runtime.StatefulKnowledgeSession
import org.drools.runtime.rule.WorkingMemoryEntryPoint
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component
/**
 * responsible for management of a drools rule session
 *
 * @author jary
 * @since 12/12/2012
 */
@Component
@Slf4j
class SessionManager {

    /** abstract the lifecycle management away from the logic found here */
    SessionLifecycleDelegate sessionDelegate

    /** entry point for the rule session, allows for tracking of origins */
    Map<String, WorkingMemoryEntryPoint> sessionEntryPoints = [:]

    /** gives us an executor to run the session on it's own thread in a managed way via spring */
    @Autowired
    ThreadPoolTaskExecutor sessionThreadManager

    /** factory responsible for setting up a CEP session for us */
    @Autowired
    RuleSessionFactory ruleSessionFactory

    /** specialized listener to handle dynamic retractions */
    @Autowired
    RetractionListener retractionListener

    /** session entry key for our main entry point */
    final static String MATCH_ENTRY = "matchInfo"

    /**
     * populate a knowledge base with out rules from classpath and construct a single session
     */
    void init() {

        sessionDelegate = new SessionLifecycleDelegate(
                ruleSessionFactory.build(new ClasspathRuleLoader()))

        // establish entry points
        setSessionEntryPoints()

        // inject global handlers needed within the rule session
        setSessionGlobals()

        // start fireUntilHalt
        sessionThreadManager.execute(sessionDelegate)
    }

    /**
     * on discard, we should ensure that we dispose of our rule session in order to prevent
     * memory leaks which may occur when populated session workingMemories are orphaned on the system
     */
    void cleanUp() {
        sessionDelegate.stop()
    }

    /**
     * give us a convenient handle to the rule session
     *
     * @return the rule session
     */
    StatefulKnowledgeSession getSession() {
        return sessionDelegate.session
    }

    /**
     * shortcut for our main session entry point
     *
     * @return main session entry point
     */
    WorkingMemoryEntryPoint getSessionEntry() {
        if (!sessionEntryPoints.containsKey(MATCH_ENTRY)) {
            throw new MissingEntryPointException(MATCH_ENTRY)
        }
        return sessionEntryPoints.get(MATCH_ENTRY)
    }

    /**
     * intercept session insertions so that we can tracks our fact handles by object
     *
     * @param object to be inserted into working memory
     */
    public void insert(Object object) {
        retractionListener.factHandleMap.put(object, session.insert(object))
    }

    /**
     * set up all the entry points we'll want in our rule session
     */
    private void setSessionEntryPoints() {
        sessionEntryPoints.put(MATCH_ENTRY, session.getWorkingMemoryEntryPoint(MATCH_ENTRY))
    }

    /**
     * add some global session variables that are needed for session abstraction of certain logic pieces
     * and container functionality (communication with chains)
     */
    private void setSessionGlobals() {

        // and give us a logger handle for rule consequences
        session.setGlobal("log", LoggerFactory.getLogger("RULE_SESSION"))
    }
}