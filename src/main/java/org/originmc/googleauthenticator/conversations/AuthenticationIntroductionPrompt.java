package org.originmc.googleauthenticator.conversations;

import net.md_5.bungee.api.chat.BaseComponent;

public class AuthenticationIntroductionPrompt implements Prompt {

    @Override
    public BaseComponent[] getPromptText(ConversationContext context) {
        return AuthenticationTexts.INTRO_MESSAGE;
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        return new AuthenticationQuitInformationPrompt();
    }

    @Override
    public boolean isInputValid(ConversationContext context, String input) {
        return input.equalsIgnoreCase("continue");
    }
}
