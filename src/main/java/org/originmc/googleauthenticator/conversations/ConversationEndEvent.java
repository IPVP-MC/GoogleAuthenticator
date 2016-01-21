package org.originmc.googleauthenticator.conversations;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

public class ConversationEndEvent extends Event {

    private ProxiedPlayer who;

    public ConversationEndEvent(ProxiedPlayer who) {
        this.who = who;
    }

    public ProxiedPlayer getPlayer() {
        return who;
    }
}
