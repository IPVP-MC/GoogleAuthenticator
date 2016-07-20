package org.ipvp.auth.conversations;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.ipvp.auth.AuthPlugin;

public class AuthenticationQuitInformationPrompt extends StringPrompt {

    private AuthPlugin plugin;

    public AuthenticationQuitInformationPrompt(AuthPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getPromptText(ConversationContext context) {
        ((Player)context.getForWhom()).spigot().sendMessage(AuthenticationTexts.QUIT_INFORMATION);
        return "";
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        if (!input.equalsIgnoreCase("continue")) {
            return this;
        }
        return new AuthenticationInstallAppPrompt(plugin);
    }
}
