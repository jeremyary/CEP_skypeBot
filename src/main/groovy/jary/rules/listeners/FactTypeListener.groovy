package jary.rules.listeners

import org.drools.event.rule.ObjectInsertedEvent
import org.drools.event.rule.ObjectRetractedEvent
import org.drools.event.rule.ObjectUpdatedEvent
import org.drools.event.rule.WorkingMemoryEventListener

/***
 * specialized WorkingMemoryEventListener that will allow us to specify a fact class by
 * simpleName and track how many times a fact of that type has been added or removed
 * from working memory - useful for testing repeat add/remove with durations
 *
 * @author jary
 * @since 01/10/2013
 */
class FactTypeListener implements WorkingMemoryEventListener {

    /** the simpleName of class type of fact we'd like to track */
    String factType

    /** map for tracking adds and removes from memory of factType facts */
    Map<String, Integer> changeCounts = ["add" : 0, "remove" : 0]

    /**
     * constructor specifying type of fact class to track
     *
     * @param factType
     */
    FactTypeListener(String factType) {
        this.factType = factType
    }

    /**
     * callback handler for when object has been placed into working memory, increment
     * tracking map 'add' count
     *
     * @param event
     */
    void objectInserted(ObjectInsertedEvent event) {
        if (event.object.class.simpleName == factType) {
            changeCounts["add"] += 1
        }
    }

    /**
     * callback handler for when object has been removed from working memory, increment
     * tracking map 'remove' count
     *
     * @param event
     */
    void objectRetracted(ObjectRetractedEvent event) {
        if (event.oldObject.class.simpleName == factType) {
            changeCounts["remove"] += 1
        }
    }

    /**
     * callback handler for when object has been updated within working memory
     * (not used for this listener, but necessary for interface)
     *
     * @param event
     */
    void objectUpdated(ObjectUpdatedEvent event) {
    }
}
