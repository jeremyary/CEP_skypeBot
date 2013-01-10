package jary
import groovy.util.logging.Slf4j
import jary.rules.SessionManager
import jary.rules.game.Fire
import jary.rules.listeners.FactTypeListener
import org.drools.time.SessionPseudoClock
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import java.util.concurrent.TimeUnit
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
class CepConceptsIntegrationTest {

    @Autowired
    SessionManager sessionManager

    SessionPseudoClock clock

    @Before
    void setUp() {
        sessionManager.init()
        // make sure session is in pseudo mode for testing
        assert sessionManager.session.sessionClock instanceof SessionPseudoClock
        clock = sessionManager.session.sessionClock
        clock.advanceTime(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
        log.debug("pseudo clock time set to {}", clock.currentTime)
    }

    @After
    void tearDown() {
        sessionManager.sessionDelegate.with {
            stop()
            while (sessionRunning.get()) {
                sleep(1000)
            }
            assert !(sessionRunning.get())
        }
    }

    @Test
    void "test set up correctly"() {}

    @Test
    void "test match expires"() {

        FactTypeListener sprinklerCount = new FactTypeListener("SprinklerInterval")
        sessionManager.session.addEventListener(sprinklerCount)

        sessionManager.insert(new Fire("foo", 10000))

        10.times {
            clock.advanceTime(1001, TimeUnit.MILLISECONDS)
            log.debug("new clock time now: ${clock.currentTime}")
            sleep(2000)
        }

        long timer = 0
        while(sprinklerCount.changeCounts["add"] < 5 && ++timer < 10) {
            sleep(1000)
        }
        assert sprinklerCount.changeCounts["add"] == 5
    }
}
