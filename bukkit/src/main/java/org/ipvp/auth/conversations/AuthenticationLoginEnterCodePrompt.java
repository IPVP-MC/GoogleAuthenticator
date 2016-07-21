package org.ipvp.auth.conversations;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.ipvp.auth.AuthPlugin;
import org.ipvp.auth.AuthenticatorCodeUtils;
import org.ipvp.auth.AuthenticationData;

public class AuthenticationLoginEnterCodePrompt extends AuthenticationEnterCodePrompt {

    public AuthenticationLoginEnterCodePrompt(AuthPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getPromptText(ConversationContext context) {
        return ChatColor.GREEN + "Please type in the code that your app is giving you.";
    }

    @Override
    public Prompt acceptValidatedInput(ConversationContext context, Number input) {
        Player player = (Player) context.getForWhom();
        AuthenticationData data = plugin.getAuthenticationData(player.getUniqueId());
        try {
            if (!AuthenticatorCodeUtils.verifyCode(data.getSecret(), input.intValue(), AuthenticatorCodeUtils.getTimeIndex(), 20)) {
                return this;
            }
        } catch (Exception e) {
            context.getForWhom().sendRawMessage(ChatColor.RED + "Failed to authenticate, please contact an administrator.");
            plugin.removeMapsFromPlayer((Player) context.getForWhom());
            e.printStackTrace();
            return END_OF_CONVERSATION;
        }
        data.setAuthenticated(true);
        data.setIp(player.getAddress().getAddress().getHostName());
        player.spigot().sendMessage(AuthenticationTexts.NOW_AUTHENTICATED);
        plugin.sendBungeeAuthentication(player);
        return END_OF_CONVERSATION; // End the conversation
    }
}
