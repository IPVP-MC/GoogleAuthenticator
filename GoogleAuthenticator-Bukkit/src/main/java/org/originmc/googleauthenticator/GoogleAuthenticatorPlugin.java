package org.originmc.googleauthenticator;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class GoogleAuthenticatorPlugin extends JavaPlugin implements PluginMessageListener {

    @Override
    public void onEnable() {
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();

        if (subchannel.equals("AuthMapGive")) {
            String username = in.readUTF();
            String URL = in.readUTF();

        } else if (subchannel.equals("AuthMapTake")) {
            String username = in.readUTF();
            // Take the map away from the user
        }
    }
}
