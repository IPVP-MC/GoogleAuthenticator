package org.ipvp.auth.conversations;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.ipvp.auth.AuthPlugin;

public class AuthenticationIntroductionPrompt extends StringPrompt {

    private AuthPlugin plugin;

    public AuthenticationIntroductionPrompt(AuthPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getPromptText(ConversationContext context) {
        ((Player)context.getForWhom()).spigot().sendMessage(AuthenticationTexts.INTRO_MESSAGE);
        return "";
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        if (!input.equalsIgnoreCase("continue")) {
            return this;
        }
        return new AuthenticationQuitInformationPrompt(plugin);
    }
}
