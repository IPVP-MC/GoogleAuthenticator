package org.originmc.googleauthenticator.conversations;

import net.md_5.bungee.api.chat.BaseComponent;
import org.originmc.googleauthenticator.GoogleAuthenticatorPlugin;

public class AuthenticationInstallAppPrompt implements Prompt {

    private GoogleAuthenticatorPlugin plugin;

    public AuthenticationInstallAppPrompt(GoogleAuthenticatorPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public BaseComponent[] getPromptText(ConversationContext context) {
        return AuthenticationTexts.INSTALL_APP_TEXT;
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        return new AuthenticationScanQRPrompt(plugin);
    }

    @Override
    public boolean isInputValid(ConversationContext context, String input) {
        return input.equalsIgnoreCase("continue");
    }
}
