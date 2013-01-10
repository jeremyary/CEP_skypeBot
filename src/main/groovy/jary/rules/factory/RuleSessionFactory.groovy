package jary.rules.factory
import groovy.util.logging.Slf4j
import jary.rules.listeners.ActivationTrackerListener
import jary.rules.listeners.DebugAgendaEventListener
import jary.rules.listeners.DebugWorkingMemoryEventListener
import jary.rules.listeners.RetractionListener
import jary.rules.loader.RuleLoader
import org.drools.KnowledgeBase
import org.drools.KnowledgeBaseConfiguration
import org.drools.KnowledgeBaseFactory
import org.drools.conf.EventProcessingOption
import org.drools.runtime.KnowledgeSessionConfiguration
import org.drools.runtime.StatefulKnowledgeSession
import org.drools.runtime.conf.ClockTypeOption
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component
/***
 * Factory to generate stateful, CEP-enabled rule sessions from a rule loader
 *
 * @author jary
 * @since 12/19/2012
 */
@Slf4j
@Component
class RuleSessionFactory {

    @Value('${rules.session.clock.type}')
    String CLOCK_TYPE

    /** we need access to the thread pool for execution of our retraction listener */
    @Autowired
    ThreadPoolTaskExecutor taskExecutor

    /** specialized listener to handle dynamic retractions */
    @Autowired
    RetractionListener retractionListener

    /** specialized listener to allow for tracking add/removes of a specific fact type */
    @Autowired
    ActivationTrackerListener activationTrackerListener

    /**
     * build out a knowledge base from our rule packages (from loader) and return a configured session
     * with listeners attached
     *
     * @param ruleLoader loader implementation that will provide knowledge packages to the session
     * @return a stateful, CEP-enabled rule session
     */
    StatefulKnowledgeSession build(RuleLoader ruleLoader) {

        // change engine mode to streaming for events (CEP)
        KnowledgeBaseConfiguration kbaseConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration()
        kbaseConfiguration.setOption(EventProcessingOption.STREAM)

        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase(kbaseConfiguration)
        knowledgeBase.addKnowledgePackages(ruleLoader.load().knowledgePackages)

        KnowledgeSessionConfiguration sessionConfiguration = KnowledgeBaseFactory.newKnowledgeSessionConfiguration()
        sessionConfiguration.setOption(ClockTypeOption.get(CLOCK_TYPE))
        log.info("set clock option type: {}", CLOCK_TYPE)

        StatefulKnowledgeSession session = knowledgeBase.newStatefulKnowledgeSession(sessionConfiguration, null)

        // throw some event listeners on the session and memory for debugging purposes -
        // will most likely coincide with a configurable parameter later
        // these attached now are used to catch the full debug from from memory and agenda
        session.addEventListener(new DebugAgendaEventListener())
        session.addEventListener(new DebugWorkingMemoryEventListener())

        // add retraction listener & start on thread for concurrency
        session.addEventListener(retractionListener)
        taskExecutor.execute(retractionListener)

        // add activation tracker listener
        session.addEventListener(activationTrackerListener)

        return session
    }
}