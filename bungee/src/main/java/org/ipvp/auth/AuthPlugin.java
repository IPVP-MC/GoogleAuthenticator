package org.ipvp.auth;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AuthPlugin extends Plugin implements Listener {

    private Map<UUID, Boolean> authenticationStatus = new ConcurrentHashMap<>();

    @Override
    public void onEnable() {
        getProxy().getPluginManager().registerListener(this, this);
    }

    @EventHandler
    public void onServerSwitch(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        
        if (player.getServer() != null && !authenticationStatus.get(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(new TextComponent(ChatColor.RED + "You cannot switch servers before authenticating."));
        }
    }
    
    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        authenticationStatus.put(event.getPlayer().getUniqueId(), false);
    }
    
    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        for (ServerInfo info : getProxy().getServers().values()) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("PlayerLogout");
            out.writeUTF(event.getPlayer().getUniqueId().toString());
            info.sendData("BungeeCord", out.toByteArray());
        }
    }
    
    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getTag().equals("BungeeCord")) {
            return;
        }

        ByteArrayDataInput input = ByteStreams.newDataInput(event.getData());
        String sub = input.readUTF();
        if (!sub.equals("Authenticated")) {
            return;
        }
        
        UUID uuid = UUID.fromString(input.readUTF());
        authenticationStatus.put(uuid, true);
    }
}
