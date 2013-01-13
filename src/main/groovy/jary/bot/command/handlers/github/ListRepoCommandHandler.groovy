package jary.bot.command.handlers.github

import com.skype.ChatMessage
import groovy.util.logging.Slf4j
import jary.bot.command.handlers.CommandHandler
import jary.bot.skype.GroupChat
import org.eclipse.egit.github.core.Repository
import org.eclipse.egit.github.core.service.RepositoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * list public github repositories for a specified user
 *
 * @author jary
 * @since 01/13/2013
 */
@Component
@Slf4j
class ListRepoCommandHandler implements CommandHandler {

    @Value('${bot.command.github.listRepos}')
    private String command

    @Autowired
    GroupChat groupChat

    Boolean isDefault() {
        return false
    }

    Boolean isMine(ChatMessage received) {
        return received.content.contains(command)
    }

    void handle(Boolean isGroupMessage, ChatMessage received) {

        String username = received.content[received.content.indexOf("for ") + 4..received.content.length() - 1]

        List<Repository> repositories = new RepositoryService().getRepositories(username)
        String response
        if (repositories.size() > 0) {
            response = "${repositories.size()} public repos found:\n"
            repositories.each {
                response += "${it.name}, created ${it.createdAt}, ${it.htmlUrl}\n"
            }
        } else {
            response = "${username} has no public repos"
        }

        if (isGroupMessage) {
            groupChat.send(response)
        } else {
            received.sender.send(response)
        }
    }
}
