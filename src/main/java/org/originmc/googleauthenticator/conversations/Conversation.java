package org.originmc.googleauthenticator.conversations;


import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Conversation class is responsible for tracking the current state of a
 * conversation, displaying prompts to the user, and dispatching the user's
 * response to the appropriate place. Conversation objects are not typically
 * instantiated directly. Instead a {@link ConversationFactory} is used to
 * construct identical conversations on demand.
 * <p>
 * Conversation flow consists of a directed graph of {@link Prompt} objects.
 * Each time a prompt gets input from the user, it must return the next prompt
 * in the graph. Since each Prompt chooses the next Prompt, complex
 * conversation trees can be implemented where the nature of the player's
 * response directs the flow of the conversation.
 * <p>
 * Each conversation has a timeout measured in the number of inactive seconds
 * to wait before abandoning the conversation. If the inactivity timeout is
 * reached, the conversation is abandoned and the user's incoming and outgoing
 * chat is returned to normal.
 * <p>
 * You should not construct a conversation manually. Instead, use the {@link
 * ConversationFactory} for access to all available options.
 */
public class Conversation {

    private Prompt firstPrompt;
    protected Prompt currentPrompt;
    protected ConversationContext context;
    protected List<ConversationCanceller> cancellers;

    /**
     * Initializes a new Conversation.
     *
     * @param plugin      The plugin that owns this conversation.
     * @param forWhom     The entity for whom this conversation is mediating.
     * @param firstPrompt The first prompt in the conversation graph.
     */
    public Conversation(Plugin plugin, ProxiedPlayer forWhom, Prompt firstPrompt) {
        this(plugin, forWhom, firstPrompt, new HashMap<>());
    }

    /**
     * Initializes a new Conversation.
     *
     * @param plugin             The plugin that owns this conversation.
     * @param forWhom            The entity for whom this conversation is mediating.
     * @param firstPrompt        The first prompt in the conversation graph.
     * @param initialSessionData Any initial values to put in the conversation
     *                           context sessionData map.
     */
    public Conversation(Plugin plugin, ProxiedPlayer forWhom, Prompt firstPrompt, Map<Object, Object> initialSessionData) {
        this.firstPrompt = firstPrompt;
        this.context = new ConversationContext(plugin, forWhom, initialSessionData);
        this.cancellers = new ArrayList<>();
    }

    /**
     * Gets the entity for whom this conversation is mediating.
     *
     * @return The entity.
     */
    public ProxiedPlayer getForWhom() {
        return context.getForWhom();
    }

    /**
     * Adds a {@link ConversationCanceller} to the cancellers collection.
     *
     * @param canceller The {@link ConversationCanceller} to add.
     */
    public void addConversationCanceller(ConversationCanceller canceller) {
        this.cancellers.add(canceller);
    }

    /**
     * Gets the list of {@link ConversationCanceller}s
     *
     * @return The list.
     */
    public List<ConversationCanceller> getCancellers() {
        return cancellers;
    }

    /**
     * Returns the Conversation's {@link ConversationContext}.
     *
     * @return The ConversationContext.
     */
    public ConversationContext getContext() {
        return context;
    }

    /**
     * Displays the first prompt of this conversation and begins redirecting
     * the user's chat responses.
     */
    public void begin() {
        if (currentPrompt == null) {
            currentPrompt = firstPrompt;
            ConversationListener.beginConversation(getForWhom(), this);
        }
    }

    /**
     * Returns Returns the current state of the conversation.
     *
     * @return The current state of the conversation.
     */
    public ConversationState getState() {
        if (currentPrompt != null) {
            return ConversationState.STARTED;
        } else {
            return ConversationState.ENDED;
        }
    }

    /**
     * Passes player input into the current prompt. The next prompt (as
     * determined by the current prompt) is then displayed to the user.
     *
     * @param input The user's chat text.
     */
    public void acceptInput(String input) {
        try { // Spigot
            if (currentPrompt != null) {
                // Test for conversation abandonment based on input
                for (ConversationCanceller canceller : cancellers) {
                    if (canceller.cancelBasedOnInput(this, input)) {
                        ProxyServer.getInstance().getPluginManager().callEvent(new ConversationEndEvent(getForWhom()));
                        return;
                    }
                }

                // Not abandoned, output the next prompt
                if (currentPrompt.isInputValid(context, input)) {
                    currentPrompt = currentPrompt.acceptInput(context, input);
                    outputCurrentPrompt();
                } else {
                    String output = currentPrompt.getFailedValidationText(context, input);
                    if (output != null && !output.isEmpty()) {
                        getForWhom().sendMessage(new TextComponent(output));
                    }
                }
            }
            // Spigot Start
        } catch (Throwable t) {
            ProxyServer.getInstance().getLogger().log(java.util.logging.Level.SEVERE, "Error handling conversation prompt", t);
        }
        // Spigot End
    }

    /**
     * Displays the next user prompt and abandons the conversation if the next
     * prompt is null.
     */
    public void outputCurrentPrompt() {
        if (currentPrompt == null) {
            ProxyServer.getInstance().getPluginManager().callEvent(new ConversationEndEvent(getForWhom()));
        } else {
            context.getForWhom().sendMessage(currentPrompt.getPromptText(context));
        }
    }

    public enum ConversationState {
        ENDED,
        STARTED
    }
}
