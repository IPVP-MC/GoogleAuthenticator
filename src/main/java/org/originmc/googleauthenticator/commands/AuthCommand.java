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
                conversation.getContext().setSessionData("authdata",
                        new AuthenticationData(AuthenticatorCodeUtils.generateNewSecret(),
                                player.getAddress().getAddress().getHostName()));
                conversation.addConversationCanceller((context, input) -> {
                    if (input.equalsIgnoreCase("exit")) {
                        TextComponent cancelMessage = new TextComponent("Cancelled two-factor setup process! Try again soon.");
                        cancelMessage.setColor(ChatColor.RED);
                        context.getForWhom().sendMessage(cancelMessage);
                        return true;
                    }
                    return false;
                });
                conversation.begin();
            } else if (args[0].equalsIgnoreCase("off")) {
                if (!plugin.hasAuthenticationData(player.getUniqueId())) {
                    player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "You need to authenticate yourself first!"));
                } else {
                    plugin.getProxy().getScheduler().runAsync(plugin, () -> {
                        plugin.removeAuthenticationData(player.getUniqueId());
                        player.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Disabled two-factor " +
                                "authentication! You can still enable it again by running \"/auth\""));
                    });
                }
            } else if (args[0].equalsIgnoreCase("ipenable")) {
                if (!plugin.hasAuthenticationData(player.getUniqueId())) {
                    player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "You need to authenticate yourself first!"));
                } else {
                    AuthenticationData data = plugin.getAuthenticationData(player.getUniqueId());
                    data.setIpTrusted(true);
                    player.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "We will now remember you by your " +
                            "IP and will no longer ask for a code every time you log in!"));
                }
            } else if (args[0].equalsIgnoreCase("ipdisable")) {
                if (!plugin.hasAuthenticationData(player.getUniqueId())) {
                    player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "You need to authenticate yourself first!"));
                } else {
                    AuthenticationData data = plugin.getAuthenticationData(player.getUniqueId());
                    data.setIpTrusted(false);
                    player.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "We will no longer remember you by your " +
                            "IP and will now require a code every time you log in!"));
                }
            } else {
                player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Unknown sub command."));
            }
        }
    }
}
