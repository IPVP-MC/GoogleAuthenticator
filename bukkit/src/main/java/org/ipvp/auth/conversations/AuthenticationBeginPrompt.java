package org.ipvp.auth.conversations;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.ipvp.auth.AuthPlugin;

public class AuthenticationBeginPrompt extends StringPrompt {

    private AuthPlugin plugin;

    public AuthenticationBeginPrompt(AuthPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getPromptText(ConversationContext context) {
        ((Player) context.getForWhom()).spigot().sendMessage(AuthenticationTexts.PRE_START_TEXT);
        return "";
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        if (!input.equalsIgnoreCase("continue")) {
            return this;
        }
        return new AuthenticationIntroductionPrompt(plugin);
    }
}
