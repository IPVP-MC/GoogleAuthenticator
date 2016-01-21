package org.originmc.googleauthenticator.conversations;

import net.md_5.bungee.api.chat.BaseComponent;

public interface Prompt {

    /**
     * Gets the text to display to the user when this prompt is first
     * presented.
     *
     * @return The text to display.
     */
    BaseComponent[] getPromptText(ConversationContext context);

    /**
     * Accepts and processes input from the user. Using the input, the next
     * Prompt in the prompt graph is returned.
     *
     * @param input   The input text from the user.
     * @return The next Prompt in the prompt graph.
     */
    Prompt acceptInput(ConversationContext context, String input);

    /**
     * Override this method to check the validity of the player's input.
     *
     * @param input The player's raw console input.
     * @return True or false depending on the validity of the input.
     */
    boolean isInputValid(ConversationContext context, String input);

    /**
     * Optionally override this method to display an additional message if the
     * user enters an invalid input.
     *
     * @param invalidInput The invalid input provided by the user.
     * @return A message explaining how to correct the input.
     */
    default String getFailedValidationText(ConversationContext context, String invalidInput) {
        return null;
    }
}
