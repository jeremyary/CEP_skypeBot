package jary

import com.skype.ChatMessage
import com.skype.ChatMessageAdapter
import com.skype.Skype
import com.skype.SkypeException
import groovy.util.logging.Slf4j
import jary.bot.BotListener
import org.eclipse.egit.github.core.*
import org.eclipse.egit.github.core.service.CommitService
import org.eclipse.egit.github.core.service.GistService
import org.eclipse.egit.github.core.service.OAuthService
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
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
    BotListener botListener

    @Autowired
    ThreadPoolTaskExecutor executor

    @Before
    void setUp() {
    }

    @Test
    void "test set up correctly"() {
        executor.execute(botListener)

        while (botListener.isRunning) {
            sleep(5000)
        }
    }

    @Test
    void "monitor for commands"() {

        Skype.daemon = false
        Skype.addChatMessageListener(new ChatMessageAdapter() {
            public void chatMessageReceived(ChatMessage received) throws SkypeException {
                if (received.type == ChatMessage.Type.SAID) {
                    if (received.content.startsWith("yo bot!")) {
                        if (received.content.contains("list repos for ")) {
                            Integer length = received.content.length() - received.content.indexOf("for ") + 4
                            String username = received.content[-length]
                            log.debug("detected: ${username}")
                        }
                    }
                }
            }
        });
    }

    @Test
    void "github commits PoC"() {

        final CommitService service = new CommitService()
        List<RepositoryCommit> commits = service.getCommits(new RepositoryId("github", "hubot"))
        commits.last().with {
            log.debug("${it.sha[0..6]} ${it.commit.author.name} ${it.commit.author.date}")
        }
    }

    @Test
    void "create gist"() {

        OAuthService oauthService = new OAuthService()
        oauthService.client.setCredentials("*", "*")
        Authorization auth = oauthService.createAuthorization(new Authorization(scopes: ["gist"]))

        GistService gistService = new GistService()
        gistService.client.OAuth2Token = auth.token

        Gist gist = gistService.createGist(
                new Gist(
                        public: false,
                        description: "created via API",
                        files: ["apigist.txt": new GistFile(content: "test gist!", filename: "apigist.txt")]
                )
        )
        log.debug("viewable at {}", gist.htmlUrl)
    }
}
