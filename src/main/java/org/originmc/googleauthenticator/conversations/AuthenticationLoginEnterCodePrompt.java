package org.originmc.googleauthenticator.conversations;

import net.md_5.bungee.api.ChatColor;
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
        return new BaseComponent[]{ new TextComponent(ChatColor.GREEN + "Please type in the code that your app is giving you.") };
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        AuthenticationData data = plugin.getAuthenticationData(context.getForWhom().getUniqueId());
        data.setAuthenticated(true);
        context.getForWhom().sendMessage(new TextComponent(ChatColor.GREEN + "You are now authenticated"));
        return null; // End the conversation
    }

    @Override
    public boolean isInputValid(ConversationContext context, String input) {
        try {
            int val = Integer.parseInt(input); // Only doing this to check if it's a valid number
            AuthenticationData data = plugin.getAuthenticationData(context.getForWhom().getUniqueId());
            return AuthenticatorCodeUtils.verifyCode(data.getSecret(), val, AuthenticatorCodeUtils.getTimeIndex(), 1);
        } catch (Exception ex) {
            return false;
        }
    }
}
