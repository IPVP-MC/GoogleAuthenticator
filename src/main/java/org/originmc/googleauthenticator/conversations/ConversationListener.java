package org.originmc.googleauthenticator.conversations;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConversationListener implements Listener {

    private static Map<UUID, Conversation> activeConversations = new HashMap<>();

    @EventHandler(priority = EventPriority.LOW)
    public void processConversationWhenChatting(ChatEvent event) {
        if (event.getSender() instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) event.getSender();

            if (activeConversations.containsKey(player.getUniqueId())) {
                event.setCancelled(true);
                Conversation conversation = activeConversations.get(player.getUniqueId());

                if (conversation.getState() == Conversation.ConversationState.ENDED) {
                    activeConversations.remove(player.getUniqueId());
                } else {
                    String input = event.getMessage();
                    conversation.acceptInput(input);
                }
            }
        }
    }

    @EventHandler
    public void removeWhenDisconnecting(PlayerDisconnectEvent event) {
        activeConversations.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void removeWhenAbandon(ConversationEndEvent event) {
        activeConversations.remove(event.getPlayer().getUniqueId());
    }

    /**
     * Begins a conversation for a player
     *
     * @param player the player
     * @param conversation the conversation
     */
    static void beginConversation(ProxiedPlayer player, Conversation conversation) {
        activeConversations.put(player.getUniqueId(), conversation);
        conversation.outputCurrentPrompt();
    }

    /**
     * Ends all conversations
     */
    public static void endAllConversations() {
        activeConversations.clear();
    }
}
