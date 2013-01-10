package jary.rules.listeners

import groovy.util.logging.Slf4j
import org.drools.event.rule.*

/**
 * full-output debugging event listener for rule session agenda
 *
 * What's an agenda? Thanks for asking...
 * Drools benefits from excellent speeds partly due to the way the ReteOO
 * algorithm is implemented. Rather than waiting and making all considerations
 * about our logic and data at the instant we give the go-ahead to fire rules,
 * the engine does a portion of the work as facts are added, change and removed
 * within Working Memory. Since our rules are already known when we start to
 * insert our data, the engine can go ahead and do some reasoning and determine,
 * based on the new state of data, which rules would fire if nothing were to change
 * between that point and our request to fire all rules. It does this by maintaining
 * an agenda. When a rule's conditions are met based on the provided data, a rule
 * activation is added to the agenda. If something changes causing that rule's
 * conditions to no longer be satisfied, then the activation is removed from the agenda.
 * When we finally get to the point that we call fireAllRules(), we're simply asking
 * the engine to go down the list of activations currently on the agenda and trigger
 * their consequences. Each time a fact is manipulated in working memory, the rule
 * conditions are reconsidered and the agenda is updated appropriately.
 *
 * @author jary
 * @since 12/16/2012
 */
@Slf4j
class DebugAgendaEventListener implements AgendaEventListener {

    /**
     * callback handler for rule activations removed from agenda
     *
     * @param event
     */
    void activationCancelled(ActivationCancelledEvent event) {
        log.info("ACTIVATION CANCELLED: ${event.activation.rule.name}")
    }

    /**
     * callback handler for rule activations added to agenda
     *
     * @param event
     */
    void activationCreated(ActivationCreatedEvent event) {
        log.info("ACTIVATION CREATED: ${event.activation.rule.name}")
    }

    /**
     * callback handler for immediately after rule consequence triggered
     *
     * @param event
     */
    void afterActivationFired(AfterActivationFiredEvent event) {
        log.info("ACTIVATION FIRED: ${event.activation.rule.name}")
    }

    /**
     * callback handler for when an agenda group is removed from roster
     * (beyond the scope of our project, but necessary from interface)
     *
     * @param event
     */
    void agendaGroupPopped(AgendaGroupPoppedEvent event) {
    }

    /**
     * callback handler for when an agenda group is rostered
     * (beyond the scope of our project, but necessary from interface)
     *
     * @param event
     */
    void agendaGroupPushed(AgendaGroupPushedEvent event) {
    }

    /**
     * callback handler for immediately before rule consequence triggered
     *
     * @param event
     */
    void beforeActivationFired(BeforeActivationFiredEvent event) {
        log.info("ACTIVATION TO FIRE ${event.activation.rule.name}")
    }

    /**
     * callback handler for when a ruleflow group is about to be activated
     * (beyond the scope of our project, but necessary from interface)
     *
     * @param event
     */
    void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
    }

    /**
     * callback handler following when a ruleflow group has activated
     * (beyond the scope of our project, but necessary from interface)
     *
     * @param event
     */
    void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
    }

    /**
     * callback handler for when a ruleflow group is about to be deactivated
     * (beyond the scope of our project, but necessary from interface)
     *
     * @param event
     */
    void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
    }

    /**
     * callback handler following when a ruleflow group has deactivated
     * (beyond the scope of our project, but necessary from interface)
     *
     * @param event
     */
    void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
    }
}