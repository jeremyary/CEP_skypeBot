package jary.bot.command.handlers

import com.skype.ChatMessage
import com.skype.Skype
import com.skype.connector.Connector
import groovy.util.logging.Slf4j
import jary.bot.skype.GroupChat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * check to see if bot is responsive
 *
 * @author jary
 * @since 01/13/2013
 */
@Component
@Slf4j
class AliveCommandHandler implements CommandHandler {

    @Value('${bot.command.alive}')
    private String command

    @Value('${bot.command.prefix}')
    private String commandPrefix

    @Autowired
    GroupChat groupChat

    Boolean isDefault() {
        return false
    }

    Boolean isMine(ChatMessage received) {
        return received.content.contains(command)
    }

    void handle(Boolean isGroupMessage, ChatMessage received) {

        String response = "yes ${received.sender.displayName}, I'm here"
        if (isGroupMessage) {
            groupChat.send(response)
        } else {
            received.sender.send(response)
        }
    }
}