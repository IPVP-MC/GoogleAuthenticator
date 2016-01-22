package org.originmc.googleauthenticator.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
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

    @EventHandler
    public void onPlayerConnect(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        plugin.getProxy().getScheduler().runAsync(plugin, () -> {
            AuthenticationData playerData = plugin.getDatabase().getAuthenticationData(uuid);
            String ip = player.getAddress().getAddress().getHostName();

            // Proceed if the player has set up 2 factor auth
            if (playerData != null) {
                plugin.addAuthenticationData(uuid, playerData);

                if (playerData.isTrustingIp() && ip.equals(playerData.getIp())) {
                    playerData.setAuthenticated(true);
                } else {
                    player.sendMessage(AuthenticationTexts.LOGIN_REQUIRES_AUTH);
                    Conversation conversation = new Conversation(plugin, player, new AuthenticationLoginEnterCodePrompt(plugin));
                    conversation.begin();
                }
            }
        });
    }

    @EventHandler
    public void onPlayerCommand(ChatEvent event) {
        // TODO: Check if the player is unauthorized - if not then don't allow the command
        if (event.getSender() instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) event.getSender();
            AuthenticationData data = plugin.getAuthenticationData(player.getUniqueId());

            if (data != null && !data.isAuthenticated()) {
                // TODO: Process data
            }
        }
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        plugin.clearAndUpdateAuthenticationData(uuid); // Remove and clear the players auth data
    }
}
