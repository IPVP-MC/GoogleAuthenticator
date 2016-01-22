package org.originmc.googleauthenticator.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import org.originmc.googleauthenticator.AuthenticationData;
import org.originmc.googleauthenticator.AuthenticatorCodeUtils;
import org.originmc.googleauthenticator.GoogleAuthenticatorPlugin;
import org.originmc.googleauthenticator.conversations.AuthenticationBeginPrompt;
import org.originmc.googleauthenticator.conversations.AuthenticationTexts;
import org.originmc.googleauthenticator.conversations.Conversation;

/**
 * Controls the /auth command and its functions
 */
public class AuthCommand extends Command {

    private GoogleAuthenticatorPlugin plugin;

    public AuthCommand(GoogleAuthenticatorPlugin plugin) {
        super("auth");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new TextComponent("Only players can use this command"));
        } else {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            if (args.length == 0) {
                // Create new authentication conversation
                Conversation conversation = new Conversation(plugin, (ProxiedPlayer) sender, new AuthenticationBeginPrompt(plugin));
                conversation.getContext().setSessionData("authdata", new AuthenticationData(AuthenticatorCodeUtils.generateNewSecret(),
                        player.getAddress().getAddress().getHostName()));
                conversation.addConversationCanceller((context, input) -> {
                    if (input.equalsIgnoreCase("exit")) {
                        player.sendMessage(AuthenticationTexts.CANCEL_SETUP);
                        return true;
                    }
                    return false;
                });
                conversation.begin();
            } else if (args[0].equalsIgnoreCase("off")) {
                if (!plugin.hasAuthenticationData(player.getUniqueId())) {
                    player.sendMessage(AuthenticationTexts.NEED_TO_AUTHENTICATE);
                } else {
                    plugin.getProxy().getScheduler().runAsync(plugin, () -> {
                        plugin.removeAuthenticationData(player.getUniqueId());
                        player.sendMessage(AuthenticationTexts.DISABLED_AUTHENTICATION);
                    });
                }
            } else if (args[0].equalsIgnoreCase("ipenable")) {
                if (!plugin.hasAuthenticationData(player.getUniqueId())) {
                    player.sendMessage(AuthenticationTexts.NEED_TO_AUTHENTICATE);
                } else {
                    AuthenticationData data = plugin.getAuthenticationData(player.getUniqueId());
                    data.setIpTrusted(true);
                    player.sendMessage(AuthenticationTexts.NOW_REMEMBERING_IP);
                }
            } else if (args[0].equalsIgnoreCase("ipdisable")) {
                if (!plugin.hasAuthenticationData(player.getUniqueId())) {
                    player.sendMessage(AuthenticationTexts.NEED_TO_AUTHENTICATE);
                } else {
                    AuthenticationData data = plugin.getAuthenticationData(player.getUniqueId());
                    data.setIpTrusted(false);
                    player.sendMessage(AuthenticationTexts.NO_LONGER_REMEMBERING_IP);
                }
            } else {
                player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Unknown sub command."));
            }
        }
    }
}
