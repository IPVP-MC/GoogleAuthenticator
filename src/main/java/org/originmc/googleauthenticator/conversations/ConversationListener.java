package org.originmc.googleauthenticator.conversations;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.Map;

public class ConversationListener implements Listener {

    private static Map<ProxiedPlayer, Conversation> activeConversations = new HashMap<>();

    @EventHandler
    public void processConversationWhenChatting(ChatEvent event) {
        if (event.getSender() instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) event.getSender();

            if (activeConversations.containsKey(player)) {
                event.setCancelled(true);
                Conversation conversation = activeConversations.get(player);

                if (conversation.getState() == Conversation.ConversationState.ENDED) {
                    activeConversations.remove(player);
                } else {
                    String input = event.getMessage();
                    conversation.acceptInput(input);
                }
            }
        }
    }

    @EventHandler
    public void removeWhenDisconnecting(PlayerDisconnectEvent event) {
        activeConversations.remove(event.getPlayer());
    }

    @EventHandler
    public void removeWhenAbandon(ConversationEndEvent event) {
        activeConversations.remove(event.getPlayer());
    }

    /**
     * Begins a conversation for a player
     *
     * @param player the player
     * @param conversation the conversation
     */
    public static void beginConversation(ProxiedPlayer player, Conversation conversation) {
        activeConversations.put(player, conversation);
        conversation.outputNextPrompt();
    }

    /**
     * Ends all conversations
     */
    public static void endAllConversations() {
        activeConversations.clear();
    }
}
