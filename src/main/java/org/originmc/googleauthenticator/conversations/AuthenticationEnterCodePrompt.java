package org.originmc.googleauthenticator.conversations;

import net.md_5.bungee.api.chat.BaseComponent;
import org.originmc.googleauthenticator.AuthenticationData;
import org.originmc.googleauthenticator.AuthenticatorCodeUtils;
import org.originmc.googleauthenticator.GoogleAuthenticatorPlugin;

public class AuthenticationEnterCodePrompt implements Prompt {

    private GoogleAuthenticatorPlugin plugin;

    public AuthenticationEnterCodePrompt(GoogleAuthenticatorPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public BaseComponent[] getPromptText(ConversationContext context) {
        return AuthenticationTexts.ENTER_CODE_TEXT;
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        AuthenticationData data = (AuthenticationData) context.getSessionData("authdata");
        plugin.addAuthenticationData(context.getForWhom().getUniqueId(), data);
        data.setAuthenticated(true);
        context.getForWhom().sendMessage(AuthenticationTexts.AUTHENTICATED_TEXT);
        return null; // End the conversation
    }

    @Override
    public boolean isInputValid(ConversationContext context, String input) {
        try {
            int val = Integer.parseInt(input); // Only doing this to check if it's a valid number
            AuthenticationData data = (AuthenticationData) context.getSessionData("authdata");
            return AuthenticatorCodeUtils.verifyCode(data.getSecret(), val, AuthenticatorCodeUtils.getTimeIndex(), 1);
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public String getFailedValidationText(ConversationContext context, String invalidInput) {
        return "";
    }
}
