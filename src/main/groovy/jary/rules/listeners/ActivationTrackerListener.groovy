package jary.rules.listeners

import org.drools.definition.rule.Rule
import org.drools.event.rule.*
import org.springframework.stereotype.Component

/***
 *
 *
 * @author jary
 * @since 01/10/2013
 */
@Component
class ActivationTrackerListener implements AgendaEventListener {

    /** running list of current activations on the agenda */
    Set<Rule> activeRules = []

    /**
     * callback handler for rule activations removed from agenda
     *
     * @param event
     */
    void activationCancelled(ActivationCancelledEvent event) {
        if (activeRules.contains(event.activation.rule)) {
            activeRules.remove(event.activation.rule)
        }
    }

    /**
     * callback handler for rule activations added to agenda
     *
     * @param event
     */
    void activationCreated(ActivationCreatedEvent event) {
        if (!activeRules.contains(event.activation.rule)) {
            activeRules.add(event.activation.rule)
        }
    }

    /**
     * callback handler for immediately after rule consequence triggered
     *
     * @param event
     */
    void afterActivationFired(AfterActivationFiredEvent event) {
        if (activeRules.contains(event.activation.rule)) {
            activeRules.remove(event.activation.rule)
        }
    }

    /**
     * callback handler for when an agenda group is removed from roster
     * (beyond the scope of listener intent, but necessary from interface)
     *
     * @param event
     */
    void agendaGroupPopped(AgendaGroupPoppedEvent event) {
    }

    /**
     * callback handler for when an agenda group is rostered
     * (beyond the scope of listener intent, but necessary from interface)
     *
     * @param event
     */
    void agendaGroupPushed(AgendaGroupPushedEvent event) {
    }

    /**
     * callback handler for immediately before rule consequence triggered
     * (beyond the scope of listener intent, but necessary from interface)
     *
     * @param event
     */
    void beforeActivationFired(BeforeActivationFiredEvent event) {
    }

    /**
     * callback handler for when a ruleflow group is about to be activated
     * (beyond the scope of listener intent, but necessary from interface)
     *
     * @param event
     */
    void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
    }

    /**
     * callback handler following when a ruleflow group has activated
     * (beyond the scope of listener intent, but necessary from interface)
     *
     * @param event
     */
    void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
    }

    /**
     * callback handler for when a ruleflow group is about to be deactivated
     * (beyond the scope of listener intent, but necessary from interface)
     *
     * @param event
     */
    void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
    }

    /**
     * callback handler following when a ruleflow group has deactivated
     * (beyond the scope of listener intent, but necessary from interface)
     *
     * @param event
     */
    void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
    }
}