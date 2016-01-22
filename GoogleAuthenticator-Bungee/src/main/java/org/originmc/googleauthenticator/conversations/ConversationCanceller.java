package org.originmc.googleauthenticator.conversations;

/**
 * A ConversationCanceller is a class that cancels an active {@link
 * Conversation}. A Conversation can have more than one ConversationCanceller.
 */
public interface ConversationCanceller {

    /**
     * Cancels a conversation based on user input.
     *
     * @param context The conversation
     * @param input The input text from the user.
     * @return True to cancel the conversation, False otherwise.
     */
    boolean cancelBasedOnInput(Conversation context, String input);
}
