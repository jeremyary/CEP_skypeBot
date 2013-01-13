package jary.bot.command.handlers

import com.skype.ChatMessage

/**
 * interface for command handlers
 *
 * @author jary
 * @since 1/13/13
 */
public interface CommandHandler {

    Boolean isDefault()

    Boolean isMine(ChatMessage received)

    void handle(Boolean isGroupMessage, ChatMessage received)
}