package org.originmc.googleauthenticator.conversations;

import net.md_5.bungee.api.chat.BaseComponent;
import org.originmc.googleauthenticator.GoogleAuthenticatorPlugin;

public class AuthenticationIntroductionPrompt implements Prompt {

    private GoogleAuthenticatorPlugin plugin;

    public AuthenticationIntroductionPrompt(GoogleAuthenticatorPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public BaseComponent[] getPromptText(ConversationContext context) {
        return AuthenticationTexts.INTRO_MESSAGE;
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        return new AuthenticationQuitInformationPrompt(plugin);
    }

    @Override
    public boolean isInputValid(ConversationContext context, String input) {
        return input.equalsIgnoreCase("continue");
    }
}
