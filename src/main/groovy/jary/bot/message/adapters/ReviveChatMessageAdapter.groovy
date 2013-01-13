package jary.bot.message.adapters

import com.skype.ChatMessage
import com.skype.ChatMessageListener
import com.skype.SkypeException
import com.skype.connector.Connector
import groovy.util.logging.Slf4j
import jary.bot.BotListener
import jary.bot.ReviveListener
import jary.bot.command.handlers.CommandHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component

/**
 * chat adapter that detects messages received from group chat window and forwards to handler
 *
 * @author jary
 * @since 01/13/2013
 */
@Component
@Slf4j
class ReviveChatMessageAdapter implements ChatMessageListener {

    /** used to detect commands intended for the bot */
    @Value('${bot.command.prefix}')
    String commandPrefix

    /** used to detect commands intended for the bot */
    @Value('${bot.command.revive}')
    String command

    /** topic for group chat window */
    @Value('${bot.owner}')
    private String owner

    /** group of all command handlers for iteration */
    @Value('${bot.chat.group.topic}')
    String groupChatTopic

    @Autowired
    BotListener botListener

    @Autowired
    ReviveListener reviveListener

    @Autowired
    ThreadPoolTaskExecutor executor

    /**
     * chat message has been detected, filter over group messages and pass to handler
     *
     * @param received chat message content
     * @throws SkypeException
     */
    void chatMessageReceived(ChatMessage received) throws SkypeException {
        if (received.type == ChatMessage.Type.SAID && received.content.startsWith(commandPrefix)) {
            if (received.sender.id == owner && received.content.contains(command)) {
                reviveListener.interrupt()
                executor.execute(botListener)
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
