package org.originmc.googleauthenticator.conversations;

import net.md_5.bungee.api.chat.BaseComponent;

public class AuthenticationInstallAppPrompt implements Prompt {

    @Override
    public BaseComponent[] getPromptText(ConversationContext context) {
        return AuthenticationTexts.INSTALL_APP_TEXT;
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        return new AuthenticationScanQRPrompt();
    }

    @Override
    public boolean isInputValid(ConversationContext context, String input) {
        return input.equalsIgnoreCase("continue");
    }
}
