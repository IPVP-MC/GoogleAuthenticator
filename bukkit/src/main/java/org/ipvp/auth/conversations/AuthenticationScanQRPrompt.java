package org.ipvp.auth.conversations;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.ipvp.auth.AuthenticatorCodeUtils;
import org.ipvp.auth.AuthenticationData;
import org.ipvp.auth.AuthPlugin;

public class AuthenticationScanQRPrompt extends StringPrompt {

    private AuthPlugin plugin;

    public AuthenticationScanQRPrompt(AuthPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getPromptText(ConversationContext context) {
        AuthenticationData data = (AuthenticationData) context.getSessionData("authdata");
        Player player = (Player) context.getForWhom();
        plugin.giveMapToPlayer(player, AuthenticatorCodeUtils.getQRBarcodeURL(player.getName(), "ipvp", data.getSecret()));
        ((Player) context.getForWhom()).spigot().sendMessage(AuthenticationTexts.OPEN_APP_TEXT);
        return "";
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        if (!input.equalsIgnoreCase("continue")) {
            return this;
        }
        return new AuthenticationEnterCodePrompt(plugin);
    }
}
