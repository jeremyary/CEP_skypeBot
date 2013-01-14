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
class TalkCommandHandler implements CommandHandler {

    @Value('${bot.command.jokes.talk}')
    private String command

    @Value('${bot.users.joke}')
    private String[] users

    @Value('${bot.command.prefix}')
    private String commandPrefix

    @Value('${bot.joke.talk.phrases}')
    private String[] phrases

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

            List<String> responses = phrases.toList()
            Collections.shuffle(responses)

            String name = received.content[(received.content.indexOf("insult") + 6)..(received.content.size()-1)]

            String response = " hey ${name}, ${responses[0]}"

            if (isGroupMessage) {
                received.sender.send(response)
            } else {
                received.sender.send(response)
            }
        } else {
            received.sender.send("I didn't understand that command. Try '$commandPrefix help'")
        }
    }
}