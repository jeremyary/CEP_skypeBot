package jary.bot.command.handlers

import com.skype.ChatMessage
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * provide information on what commands bot can handle
 *
 * @author jary
 * @since 01/13/2013
 */
@Component
@Slf4j
class HelpCommandHandler implements CommandHandler {

    @Value('${bot.command.help}')
    private String command

    /** used to detect commands intended for the bot */
    @Value('${bot.command.prefix}')
    private String commandPrefix

    Boolean isDefault() {
        return false
    }

    Boolean isMine(ChatMessage received) {
        return received.content.contains(command)
    }

    void handle(Boolean isGroupMessage, ChatMessage received) {
        received.sender.send("""${commandPrefix} has the following commands:

            ${commandPrefix} list repos for [github username]
            ${commandPrefix} help"""
        )
    }
}
