package jary.bot.command.handlers

import com.skype.ChatMessage
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
class RollCommandHandler implements CommandHandler {

    @Value('${bot.command.jokes.roll}')
    private String command

    @Value('${bot.users.joke}')
    private String[] users

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

        if ((users as Set).contains(received.sender.id)) {
            if (isGroupMessage) {
                groupChat.send("*ahem* I don't know what you're talking about")
                received.sender.send("ok maybe I do...puff puff pass mofo!")
            } else {
                received.sender.send("puff puff pass mofo!")
            }
        } else {
            received.sender.send("I didn't understand that command. Try '$commandPrefix help'")
        }
    }
}