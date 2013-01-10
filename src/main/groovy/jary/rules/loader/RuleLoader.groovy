package jary.rules.loader

import org.drools.builder.KnowledgeBuilder

/***
 * provide interface for loading rule files from various locations (classpath, persistence, outside war, etc.)
 * and types (DRL, XML, etc.)
 *
 * @author jary
 * @since 12/19/2012
 */
public interface RuleLoader {

    /**
     * Provider a {@link org.drools.builder.KnowledgeBuilder} with rule resources loaded in
     *
     * @return knowledge builder populated with rules, ready for configuration
     */
    KnowledgeBuilder load()
}