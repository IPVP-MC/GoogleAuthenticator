package org.originmc.googleauthenticator.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import org.originmc.googleauthenticator.AuthenticationData;
import org.originmc.googleauthenticator.GoogleAuthenticatorPlugin;
import org.originmc.googleauthenticator.conversations.AuthenticationLoginEnterCodePrompt;
import org.originmc.googleauthenticator.conversations.AuthenticationTexts;
import org.originmc.googleauthenticator.conversations.Conversation;

import java.util.UUID;

/**
 * Handles player authentication functions
 */
public class PlayerListener implements Listener {

    private GoogleAuthenticatorPlugin plugin;

    public PlayerListener(GoogleAuthenticatorPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerConnect(LoginEvent event) {
        if (event.isCancelled()) {
            return;
        }

        event.registerIntent(plugin);
        UUID uuid = event.getConnection().getUniqueId();
        AuthenticationData playerData = plugin.getDatabase().getAuthenticationData(uuid);
        String ip = event.getConnection().getAddress().getAddress().getHostName();

        // Proceed if the player has set up 2 factor auth
        if (playerData != null) {
            if (playerData.isTrustingIp() && ip.equals(playerData.getIp())) {
                playerData.setAuthenticated(true);
            }
            plugin.addAuthenticationData(uuid, playerData);
        }
        event.completeIntent(plugin);
    }

    @EventHandler
    public void onPlayerConnect(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        AuthenticationData data = plugin.getAuthenticationData(uuid);

        if (data != null) {
            if (data.isAuthenticated()) {
                player.sendMessage(AuthenticationTexts.NOW_AUTHENTICATED);
            } else {
                player.sendMessage(AuthenticationTexts.LOGIN_REQUIRES_AUTH);
                Conversation conversation = new Conversation(plugin, player, new AuthenticationLoginEnterCodePrompt(plugin));
                conversation.begin();
            }
        }
    }

    @EventHandler
    public void onPlayerCommand(ChatEvent event) {
        if (event.getSender() instanceof ProxiedPlayer && !event.isCancelled()) {
            ProxiedPlayer player = (ProxiedPlayer) event.getSender();
            AuthenticationData data = plugin.getAuthenticationData(player.getUniqueId());

            if (data != null && !data.isAuthenticated()) {
                event.setCancelled(true);
                player.sendMessage(AuthenticationTexts.NEED_TO_AUTHENTICATE);
            }
        }
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        plugin.getProxy().getScheduler().runAsync(plugin, () -> plugin.clearAndUpdateAuthenticationData(uuid)); // Remove and clear the players auth data
    }

    @EventHandler
    public void onPlayerSwitchServers(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        AuthenticationData data = plugin.getAuthenticationData(player.getUniqueId());

        if (data != null && !data.isAuthenticated()) {
            if (!event.getTarget().getName().equals("hub1") && !event.getTarget().getName().equals("hub2")) {
                event.setCancelled(true);
            }
            player.sendMessage(AuthenticationTexts.NEED_TO_AUTHENTICATE);
        }
    }
}
