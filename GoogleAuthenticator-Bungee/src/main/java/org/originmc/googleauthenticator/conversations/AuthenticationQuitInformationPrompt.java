package org.originmc.googleauthenticator.conversations;

import net.md_5.bungee.api.chat.BaseComponent;
import org.originmc.googleauthenticator.GoogleAuthenticatorPlugin;

public class AuthenticationQuitInformationPrompt implements Prompt {

    private GoogleAuthenticatorPlugin plugin;

    public AuthenticationQuitInformationPrompt(GoogleAuthenticatorPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public BaseComponent[] getPromptText(ConversationContext context) {
        return AuthenticationTexts.QUIT_INFORMATION;
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        return new AuthenticationInstallAppPrompt(plugin);
    }

    @Override
    public boolean isInputValid(ConversationContext context, String input) {
        return input.equalsIgnoreCase("continue");
    }
}
