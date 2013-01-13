package jary.bot.command.handlers

import com.skype.ChatMessage
import jary.bot.skype.GroupChat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 *
 *
 * @author jary
 * @since 01/13/2013
 */
@Component
class LeaveCommandHandler implements CommandHandler {

    @Autowired
    GroupChat groupChat

    @Value('${bot.command.leave}')
    private String command

    @Value('${bot.command.prefix}')
    private String commandPrefix

    @Value('${bot.owner}')
    private String owner

    Boolean isDefault() {
        return false
    }

    Boolean isMine(ChatMessage received) {
        return received.content.contains(command)
    }

    void handle(Boolean isGroupMessage, ChatMessage received) {
        if (isGroupMessage) {
            if (received.sender.id == owner) {
                groupChat.leave()
            } else {
                groupChat.send("sorry, not authorized")
            }
        } else {
            received.sender.send("""${commandPrefix} has the following commands:

            ${commandPrefix} list repos for [github username]
            ${commandPrefix} help"""
            )
        }
    }
}
