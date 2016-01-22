package org.originmc.googleauthenticator.conversations;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.originmc.googleauthenticator.AuthenticationData;
import org.originmc.googleauthenticator.AuthenticatorCodeUtils;
import org.originmc.googleauthenticator.GoogleAuthenticatorPlugin;

public class AuthenticationScanQRPrompt implements Prompt {

    private GoogleAuthenticatorPlugin plugin;

    public AuthenticationScanQRPrompt(GoogleAuthenticatorPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public BaseComponent[] getPromptText(ConversationContext context) {
        AuthenticationData data = (AuthenticationData) context.getSessionData("authdata");
        ProxiedPlayer player = context.getForWhom();
        plugin.sendQRCodeMapToPlayer(player, AuthenticatorCodeUtils.getQRBarcodeURL(player.getName(), "origin", data.getSecret()));
        return AuthenticationTexts.OPEN_APP_TEXT;
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        return new AuthenticationEnterCodePrompt(plugin);
    }

    @Override
    public boolean isInputValid(ConversationContext context, String input) {
        return input.equalsIgnoreCase("continue");
    }
}
