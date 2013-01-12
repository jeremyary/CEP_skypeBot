package jary

import com.skype.Skype
import com.skype.connector.Connector
import groovy.util.logging.Slf4j
import jary.rules.SessionManager
import org.drools.time.SessionPseudoClock
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
/***
 * test class to prove some PoC around retractable facts with dynamic durations specified
 * on the fact as a property
 *
 * @author jary
 * @since 12/19/2012
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = ["classpath:spring.xml", "classpath:spring-test-properties.xml"])
class SkypeBotIntegrationTest {

    @Autowired
    SessionManager sessionManager

    SessionPseudoClock clock

    @Before
    void setUp() {
    }

    @Test
    void "test set up correctly"() {

        Connector connector = Skype.getConnectorInstance()

        String response = connector.execute("SEARCH GROUPS")
        assert response
    }
}
