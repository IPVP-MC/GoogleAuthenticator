package org.originmc.googleauthenticator.conversations;

import net.md_5.bungee.api.chat.BaseComponent;

public class AuthenticationQuitInformationPrompt implements Prompt {

    @Override
    public BaseComponent[] getPromptText(ConversationContext context) {
        return AuthenticationTexts.QUIT_INFORMATION;
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        return new AuthenticationInstallAppPrompt();
    }

    @Override
    public boolean isInputValid(ConversationContext context, String input) {
        return input.equalsIgnoreCase("continue");
    }
}
