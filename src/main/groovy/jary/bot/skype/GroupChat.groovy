package jary.bot.skype

import com.skype.Chat
import com.skype.Skype
import com.skype.connector.Connector
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 *
 *
 * @author jary
 * @since 1/13/13
 */
@Component
@Slf4j
class GroupChat {

    /** topic for group chat window */
    @Value('${bot.chat.group.topic}')
    String groupChatTopic

    Connector connector

    void send(String message) {
        findGroupChat().send(message)
    }

    void leave() {
        findGroupChat().leave()
    }

    private Chat findGroupChat() {

        connector = Skype.getConnectorInstance()
        connector.connect()

        String responseHeader = "CHATS "
        String response = connector.execute("SEARCH BOOKMARKEDCHATS", responseHeader)
        List<String> chatIds = response.substring(responseHeader.length()).split(", ")
        List<Chat> chats = []
        chatIds.each {
            chats.add(Chat.getInstance(it))
        }

        Chat groupChat
        chats.each {
            if (it.topic == groupChatTopic) {
                groupChat = it
            }
        }
        return groupChat
    }
}
