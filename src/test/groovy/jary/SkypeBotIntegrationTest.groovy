package jary

import groovy.util.logging.Slf4j
import jary.bot.BotListener
import org.eclipse.egit.github.core.*
import org.eclipse.egit.github.core.service.CommitService
import org.eclipse.egit.github.core.service.GistService
import org.eclipse.egit.github.core.service.OAuthService
import org.eclipse.egit.github.core.service.RepositoryService
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
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

    @Value('${auth.github.username}')
    String githubUsername

    @Value('${auth.github.password}')
    String githubPassword

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
    void "github commits PoC"() {

        RepositoryService repositoryService = new RepositoryService()
        repositoryService.client.setCredentials(githubUsername, githubPassword)

//        List<Repository> repositories = repositoryService.getOrgRepositories("CassidianCommunications")
//        Repository oasisRepo = repositories.find { it.name == "oasis" }

        List<Repository> repositories = new RepositoryService().getRepositories("jeremyary")
        Repository repository = repositories.find { it.name == "CEP_skypeBot" }

        final CommitService service = new CommitService()
        service.client.setCredentials(githubUsername, githubPassword)

        RepositoryCommit lastKnownCommit
        List<RepositoryCommit> commits

        while (true) {

            RepositoryCommit lastCommit = (service.getCommits(repository)).last()
            if (!lastKnownCommit) {
                lastKnownCommit = lastCommit
                log.debug("setting last commit - ${lastCommit.sha[0..6]} ${lastCommit.commit.author.name} ${lastCommit.commit.author.date}")
            } else if (lastCommit.sha != lastKnownCommit.sha) {
                log.debug("found a new commit")
                lastKnownCommit = lastCommit
                log.debug("${lastCommit.sha[0..6]} ${lastCommit.commit.author.name} ${lastCommit.commit.author.date}")
            }
            sleep(10000)
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
