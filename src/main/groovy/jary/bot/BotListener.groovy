package jary.bot

import com.skype.Profile
import com.skype.Skype
import groovy.util.logging.Slf4j
import jary.bot.message.adapters.CallPreventionAdapter
import jary.bot.message.adapters.GlobalChatMessageAdapter

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * set up listeners for group and single chats
 *
 * @author jary
 * @since 1/12/13
 */
@Component
@Slf4j
class BotListener implements Runnable {

    @Autowired
    GlobalChatMessageAdapter chatAdapter

    @Autowired
    CallPreventionAdapter callAdapter

    Boolean isRunning = true

    void run() {

        Skype.profile.setStatus(Profile.Status.ONLINE)

        Skype.daemon = false
        Skype.addChatMessageListener(chatAdapter)
        Skype.addCallListener(callAdapter)
    }

    void interrupt() {
        try {
            Skype.removeChatMessageListener(chatAdapter)
            Skype.removeCallListener(callAdapter)
            Skype.daemon = true
            Thread.currentThread().interrupt()
        } catch (MissingPropertyException e) {
            log.debug("interrupting bot listener")
        }
    }
}