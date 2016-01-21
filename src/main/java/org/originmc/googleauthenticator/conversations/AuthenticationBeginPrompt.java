package org.originmc.googleauthenticator.conversations;

import net.md_5.bungee.api.chat.BaseComponent;

public class AuthenticationBeginPrompt implements Prompt {

    @Override
    public BaseComponent[] getPromptText(ConversationContext context) {
        return AuthenticationTexts.PRE_START_TEXT;
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        return new AuthenticationIntroductionPrompt();
    }

    @Override
    public boolean isInputValid(ConversationContext context, String input) {
        return input.equalsIgnoreCase("continue");
    }
}
