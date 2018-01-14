package org.ipvp.auth.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.ipvp.auth.AuthenticatorCodeUtils;
import org.ipvp.auth.AuthenticationData;
import org.ipvp.auth.AuthPlugin;
import org.ipvp.auth.conversations.AuthenticationTexts;
import org.ipvp.auth.conversations.ConversationStarter;

/**
 * Controls the /auth command and its functions
 */
public class AuthCommand implements CommandExecutor {

    private AuthPlugin plugin;

    public AuthCommand(AuthPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command");
        } else {
            Player player = (Player) sender;
            if (args.length == 0 || args[0].trim().isEmpty()) {
                // Create new authentication conversation
                Conversation conversation = ConversationStarter.beginAuthentication(plugin, player);
                conversation.getContext().setSessionData("authdata", new AuthenticationData(AuthenticatorCodeUtils.generateNewSecret(),
                        player.getAddress().getAddress().getHostName()));
                plugin.registerConversation(player.getUniqueId(), conversation);
            } else if (args[0].equalsIgnoreCase("off")) {
                if (!plugin.hasAuthenticationData(player.getUniqueId())) {
                    player.spigot().sendMessage(AuthenticationTexts.NEED_TO_AUTHENTICATE);
                } else {
                    plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                        plugin.removeAuthenticationData(player.getUniqueId());
                        player.spigot().sendMessage(AuthenticationTexts.DISABLED_AUTHENTICATION);
                        if (player.hasPermission("2fa.require")) {
                            plugin.removeBungeeAuthentication(player);
                            player.sendMessage(ChatColor.RED + "You are required to set up 2-factor authentication by the " +
                                    "network. Please begin the process by using the /auth command.");
                        }
                    });
                }
            } else if (args[0].equalsIgnoreCase("ipenable")) {
                if (!plugin.hasAuthenticationData(player.getUniqueId())) {
                    player.spigot().sendMessage(AuthenticationTexts.NEED_TO_AUTHENTICATE);
                } else {
                    AuthenticationData data = plugin.getAuthenticationData(player.getUniqueId());
                    data.setIpTrusted(true);
                    plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> plugin.getController().updateAuthenticationData(player.getUniqueId(), data));
                    player.spigot().sendMessage(AuthenticationTexts.NOW_REMEMBERING_IP);
                }
            } else if (args[0].equalsIgnoreCase("ipdisable")) {
                if (!plugin.hasAuthenticationData(player.getUniqueId())) {
                    player.spigot().sendMessage(AuthenticationTexts.NEED_TO_AUTHENTICATE);
                } else {
                    AuthenticationData data = plugin.getAuthenticationData(player.getUniqueId());
                    data.setIpTrusted(false);
                    plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> plugin.getController().updateAuthenticationData(player.getUniqueId(), data));
                    player.spigot().sendMessage(AuthenticationTexts.NO_LONGER_REMEMBERING_IP);
                }
            } else {
                player.sendMessage(ChatColor.RED + "Unknown sub command.");
            }
        }
        return true;
    }
}
