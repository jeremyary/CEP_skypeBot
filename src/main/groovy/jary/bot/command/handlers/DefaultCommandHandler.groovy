package jary.bot.command.handlers

import com.skype.ChatMessage
import groovy.util.logging.Slf4j
import jary.bot.skype.GroupChat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * provide a default in case no other handlers have been found
 *
 * @author jary
 * @since 01/13/2013
 */
@Component
@Slf4j
class DefaultCommandHandler implements CommandHandler {

    @Autowired
    GroupChat groupChat

    /** used to detect commands intended for the bot */
    @Value('${bot.command.prefix}')
    private String commandPrefix

    Boolean isDefault() {
        return true
    }

    Boolean isMine(ChatMessage received) {
        return false
    }

    void handle(Boolean isGroupMessage, ChatMessage received) {

        String response = "I didn't understand that command. Try '$commandPrefix help'"
        if (isGroupMessage) {
            groupChat.send(response)
        } else {
            received.sender.send(response)
        }
    }
}