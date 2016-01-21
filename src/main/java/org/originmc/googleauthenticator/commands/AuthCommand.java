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
        } else if (args.length == 0) {
            ProxiedPlayer player = (ProxiedPlayer) sender;

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

        } else if (args[0].equalsIgnoreCase("ipenable")) {

        } else if (args[0].equalsIgnoreCase("ipdisable")) {

        }
    }
}
