package org.ipvp.auth.conversations;

import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.ipvp.auth.AuthPlugin;

public class ConversationStarter {
    
    public static Conversation beginAuthentication(AuthPlugin plugin, Player player) {
        org.bukkit.conversations.Conversation converstion = new ConversationFactory(plugin)
                .withModality(true) // Suppress messages
                .withLocalEcho(false)
                .withFirstPrompt(new AuthenticationBeginPrompt(plugin))
                .withEscapeSequence("exit")
                .withEscapeSequence("quit")
                .withEscapeSequence("stop")
                .withEscapeSequence("cancel")
                .addConversationAbandonedListener(event -> {
                    if (!event.gracefulExit()) {
                        player.spigot().sendMessage(AuthenticationTexts.CANCEL_SETUP);
                    }
                    plugin.removeMapsFromPlayer(player);
                    plugin.removeConversation(((Player) event.getContext().getForWhom()).getUniqueId());
                })
                .buildConversation(player);
        converstion.begin();
        return converstion;
    }
    
    public static Conversation askForCode(AuthPlugin plugin, Player player) {
        Conversation converstion = new ConversationFactory(plugin)
                .withModality(true) // Suppress messages
                .withLocalEcho(false)
                .withFirstPrompt(new AuthenticationLoginEnterCodePrompt(plugin))
                .withEscapeSequence("exit")
                .withEscapeSequence("quit")
                .withEscapeSequence("stop")
                .withEscapeSequence("cancel")
                .addConversationAbandonedListener(event -> {
                    if (!event.gracefulExit()) {
                        player.kickPlayer("You failed to enter a valid authentication code");
                    }
                    plugin.removeMapsFromPlayer(player);
                    plugin.removeConversation(((Player) event.getContext().getForWhom()).getUniqueId());
                })
                .buildConversation(player);
        converstion.begin();
        return converstion;
    }

}
