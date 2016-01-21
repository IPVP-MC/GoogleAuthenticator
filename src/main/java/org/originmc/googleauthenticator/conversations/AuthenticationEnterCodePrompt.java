package org.originmc.googleauthenticator.conversations;

import net.md_5.bungee.api.chat.BaseComponent;
import org.originmc.googleauthenticator.AuthenticationData;
import org.originmc.googleauthenticator.AuthenticatorCodeUtils;
import org.originmc.googleauthenticator.GoogleAuthenticatorPlugin;

import java.util.logging.Level;

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
        int val = Integer.parseInt(input);
        AuthenticationData data = (AuthenticationData) context.getSessionData("authdata");

        try {
            if (AuthenticatorCodeUtils.verifyCode(data.getSecret(), val, AuthenticatorCodeUtils.getTimeIndex(), 1)) {
                // TODO: Message that they have been authenticated
                plugin.addAuthenticationData(context.getForWhom().getUniqueId(), data);
                data.setAuthenticated(true);
                return null;
            }
        } catch (Exception ex) {
            plugin.getLogger().log(Level.SEVERE, "Failed to verify auth code", ex);
        }

        return this;
    }

    @Override
    public boolean isInputValid(ConversationContext context, String input) {
        try {
            Integer.parseInt(input); // Only doing this to check if it's a valid number
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    @Override
    public String getFailedValidationText(ConversationContext context, String invalidInput) {
        return "";
    }
}
