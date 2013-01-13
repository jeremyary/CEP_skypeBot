package jary.bot.message.adapters

import com.skype.ChatMessage
import com.skype.ChatMessageListener
import com.skype.Skype
import com.skype.SkypeException
import com.skype.connector.Connector
import groovy.util.logging.Slf4j
import jary.bot.command.handlers.CommandHandler
import jary.bot.skype.GroupChat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * chat adapter that detects messages received from group chat window and forwards to handler
 *
 * @author jary
 * @since 01/13/2013
 */
@Component
@Slf4j
class GlobalChatMessageAdapter implements ChatMessageListener {

    /** used to detect commands intended for the bot */
    @Value('${bot.command.prefix}')
    String commandPrefix

    /** topic for group chat window */
    @Value('${bot.chat.group.topic}')
    String groupChatTopic

    /** group of all command handlers for iteration */
    @Autowired
    List<CommandHandler> commandHandlers

    /**
     * chat message has been detected, filter over group messages and pass to handler
     *
     * @param received chat message content
     * @throws SkypeException
     */
    void chatMessageReceived(ChatMessage received) throws SkypeException {
        if (received.type == ChatMessage.Type.SAID && received.content.startsWith(commandPrefix)) {

            if (received.chat.topic == groupChatTopic) {
                log.debug("group chat command: ${received.content}")
                CommandHandler handler = commandHandlers.find { it.isMine(received) }
                if (!handler) {
                    handler = commandHandlers.find { it.isDefault() }
                }
                handler.handle(true, received)
            } else {
                log.debug("solo chat command: ${received.content}")
                CommandHandler handler = commandHandlers.find { it.isMine(received) }
                if (!handler) {
                    handler = commandHandlers.find { it.isDefault() }
                }
                handler.handle(false, received)
            }
        }
    }

    /**
     * not required for scope yet
     *
     * @param chatMessage
     * @throws SkypeException
     */
    void chatMessageSent(ChatMessage chatMessage) throws SkypeException {}
}
