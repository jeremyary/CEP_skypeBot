package jary.bot.command.handlers

import com.skype.ChatMessage
import com.skype.Profile
import com.skype.Skype
import groovy.util.logging.Slf4j
import jary.bot.BotListener
import jary.bot.ReviveListener
import jary.bot.message.adapters.GlobalChatMessageAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component

/**
 * check to see if bot is responsive
 *
 * @author jary
 * @since 01/13/2013
 */
@Component
@Slf4j
class QuitCommandHandler implements CommandHandler {

    @Value('${bot.command.quit}')
    private String command

    @Value('${bot.owner}')
    private String owner

    @Autowired
    GlobalChatMessageAdapter chatAdapter


    @Autowired
    BotListener botListener

    @Autowired
    ReviveListener reviveListener

    @Autowired
    ThreadPoolTaskExecutor executor

    Boolean isDefault() {
        return false
    }

    Boolean isMine(ChatMessage received) {
        return received.content.contains(command)
    }

    void handle(Boolean isGroupMessage, ChatMessage received) {

        if (received.sender.id == owner) {
            botListener.interrupt()
            executor.execute(reviveListener)
        }
    }
}