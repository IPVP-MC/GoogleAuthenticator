package org.ipvp.auth.conversations;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.ipvp.auth.AuthPlugin;
import org.ipvp.auth.AuthenticatorCodeUtils;
import org.ipvp.auth.AuthenticationData;

public class AuthenticationEnterCodePrompt extends NumericPrompt {

    protected AuthPlugin plugin;

    public AuthenticationEnterCodePrompt(AuthPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getPromptText(ConversationContext context) {
        ((Player) context.getForWhom()).spigot().sendMessage(AuthenticationTexts.ENTER_CODE_TEXT);
        return "";
    }

    @Override
    public Prompt acceptValidatedInput(ConversationContext context, Number input) {
        AuthenticationData data = (AuthenticationData) context.getSessionData("authdata");
        try {
            if (!AuthenticatorCodeUtils.verifyCode(data.getSecret(), input.intValue(), AuthenticatorCodeUtils.getTimeIndex(), 20)) {
                return this;
            }
        } catch (Exception e) {
            context.getForWhom().sendRawMessage(ChatColor.RED + "Failed to authenticate, please contact an administrator.");
            plugin.removeMapsFromPlayer((Player) context.getForWhom());
            e.printStackTrace();
            return Prompt.END_OF_CONVERSATION;
        }
        Player player = (Player) context.getForWhom();
        plugin.addAuthenticationData(player.getUniqueId(), data);
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> plugin.getController().updateAuthenticationData(player.getUniqueId(), data));
        plugin.removeMapsFromPlayer(player);
        data.setAuthenticated(true);
        data.setIp(player.getAddress().getAddress().getHostName());
        player.spigot().sendMessage(AuthenticationTexts.AUTHENTICATED_TEXT);
        plugin.sendBungeeAuthentication(player);
        return Prompt.END_OF_CONVERSATION; // End the conversation
    }
}
