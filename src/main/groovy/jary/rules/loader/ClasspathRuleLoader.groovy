package jary.rules.loader

import org.drools.builder.KnowledgeBuilder
import org.drools.builder.KnowledgeBuilderFactory
import org.drools.builder.ResourceType
import org.drools.io.impl.ClassPathResource

/***
 * rule loader that will take the specified rule files found within the classpath and load them into the returned
 * knowledge builder
 *
 * @author jary
 * @since 12/19/2012
 */
class ClasspathRuleLoader implements RuleLoader {

    /** list of classpath DRL (rule) files to be loaded to knowledge base populating the activation rule session */
    List<String> classpathRuleFilenames = ["jary/rules/cep/cep_rules.drl"]

    /** builder that will accept our rule resources */
    KnowledgeBuilder builder

    /**
     * get rules off classpath, populate a new builder, and return it
     *
     * @return knowledge builder with rules added in
     */
    KnowledgeBuilder load() {

        builder = KnowledgeBuilderFactory.newKnowledgeBuilder()

        classpathRuleFilenames.each {
            builder.add(new ClassPathResource(it, getClass()), ResourceType.DRL)
            if (builder.hasErrors()) {
                throw new RuntimeException(builder.getErrors().toString())
            }
        }
        return builder
    }
}