package org.originmc.googleauthenticator.conversations;

import net.md_5.bungee.api.chat.BaseComponent;
import org.originmc.googleauthenticator.GoogleAuthenticatorPlugin;

public class AuthenticationScanQRPrompt implements Prompt {

    private GoogleAuthenticatorPlugin plugin;

    public AuthenticationScanQRPrompt(GoogleAuthenticatorPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public BaseComponent[] getPromptText(ConversationContext context) {
        return AuthenticationTexts.OPEN_APP_TEXT;
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        return null; // TODO: when they finish we need to verify their auth
    }

    @Override
    public boolean isInputValid(ConversationContext context, String input) {
        return input.equalsIgnoreCase("continue");
    }
}
