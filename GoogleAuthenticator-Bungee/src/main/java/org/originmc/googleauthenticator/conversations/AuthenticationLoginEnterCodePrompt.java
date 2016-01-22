package org.originmc.googleauthenticator.conversations;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.originmc.googleauthenticator.AuthenticationData;
import org.originmc.googleauthenticator.AuthenticatorCodeUtils;
import org.originmc.googleauthenticator.GoogleAuthenticatorPlugin;

public class AuthenticationLoginEnterCodePrompt extends AuthenticationEnterCodePrompt {

    public AuthenticationLoginEnterCodePrompt(GoogleAuthenticatorPlugin plugin) {
        super(plugin);
    }

    @Override
    public BaseComponent[] getPromptText(ConversationContext context) {
        return AuthenticationTexts.LOGIN_REQUIRES_AUTH_ASK;
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        AuthenticationData data = plugin.getAuthenticationData(context.getForWhom().getUniqueId());
        data.setAuthenticated(true);
        context.getForWhom().sendMessage(AuthenticationTexts.NOW_AUTHENTICATED);
        return null; // End the conversation
    }

    @Override
    public boolean isInputValid(ConversationContext context, String input) {
        try {
            int val = Integer.parseInt(input); // Only doing this to check if it's a valid number
            AuthenticationData data = plugin.getAuthenticationData(context.getForWhom().getUniqueId());
            return AuthenticatorCodeUtils.verifyCode(data.getSecret(), val, AuthenticatorCodeUtils.getTimeIndex(), 7);
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public String getFailedValidationText(ConversationContext context, String invalidInput) {
        return TextComponent.toLegacyText(AuthenticationTexts.LOGIN_REQUIRES_AUTH_ASK);
    }
}
