package org.ipvp.auth.listeners;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.ipvp.auth.conversations.ConversationStarter;
import org.ipvp.auth.AuthenticationData;
import org.ipvp.auth.AuthPlugin;
import org.ipvp.auth.conversations.AuthenticationTexts;

import java.util.UUID;
import java.util.logging.Level;

/**
 * Handles player authentication functions
 */
public class PlayerListener implements Listener {

    private AuthPlugin plugin;

    public PlayerListener(AuthPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerConnect(AsyncPlayerPreLoginEvent event) {
        try {
            UUID uuid = event.getUniqueId();
            if (plugin.getAuthenticationData(uuid) != null) {
                return;
            }
            AuthenticationData playerData = plugin.getController().getAuthenticationData(uuid);
            String ip = event.getAddress().getHostName();

            // Proceed if the player has set up 2 factor auth
            if (playerData != null) {
                if (playerData.isTrustingIp() && ip.equals(playerData.getIp())) {
                    playerData.setAuthenticated(true);
                }
                plugin.addAuthenticationData(uuid, playerData);
            }
        } catch (Throwable t) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get authentication data", t);
        }
    }

    @EventHandler
    public void onPlayerConnect(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        AuthenticationData data = plugin.getAuthenticationData(uuid);

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (data == null) {
                plugin.sendBungeeAuthentication(player);
                player.sendMessage(ChatColor.RED + "Secure your account with 2-factor authentication using /auth.");
            } else if (data.isAuthenticated()) {
                player.spigot().sendMessage(AuthenticationTexts.NOW_AUTHENTICATED);
                plugin.sendBungeeAuthentication(player);
            } else {
                if (plugin.getAuthenticationData(uuid) == null) { // Order of operation issues with logout message
                    plugin.addAuthenticationData(uuid, data); 
                }
                player.spigot().sendMessage(AuthenticationTexts.LOGIN_REQUIRES_AUTH);
                Conversation conversation = ConversationStarter.askForCode(plugin, player);
                plugin.registerConversation(player.getUniqueId(), conversation);
            }
        }, 20L);
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        AuthenticationData data = plugin.getAuthenticationData(player.getUniqueId());

        if (data != null && !data.isAuthenticated()) {
            event.setCancelled(true);
            player.spigot().sendMessage(AuthenticationTexts.NEED_TO_AUTHENTICATE);
        }
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        if (!plugin.getConfig().getBoolean("bungee", true)) {
            Player player = event.getPlayer();
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> plugin.clearAndUpdateAuthenticationData(player.getUniqueId()));
        }
    }
}
