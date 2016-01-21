package org.originmc.googleauthenticator.conversations;

import net.md_5.bungee.api.chat.BaseComponent;
import org.originmc.googleauthenticator.GoogleAuthenticatorPlugin;

public class AuthenticationBeginPrompt implements Prompt {

    private GoogleAuthenticatorPlugin plugin;

    public AuthenticationBeginPrompt(GoogleAuthenticatorPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public BaseComponent[] getPromptText(ConversationContext context) {
        return AuthenticationTexts.PRE_START_TEXT;
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        return new AuthenticationIntroductionPrompt(plugin);
    }

    @Override
    public boolean isInputValid(ConversationContext context, String input) {
        return input.equalsIgnoreCase("continue");
    }
}
