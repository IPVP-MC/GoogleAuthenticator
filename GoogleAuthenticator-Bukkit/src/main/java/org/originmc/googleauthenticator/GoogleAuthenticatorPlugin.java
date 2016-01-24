package org.originmc.googleauthenticator;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;
import java.util.logging.Level;

public class GoogleAuthenticatorPlugin extends JavaPlugin implements PluginMessageListener, Listener {

    /**
     * The display name of all maps that are given out using {@link #giveMapToPlayer(Player, String)}
     */
    public static final String QR_CODE_MAP_NAME = ChatColor.GOLD + "Google Authenticator Map";

    @Override
    public void onEnable() {
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        removeMapsFromPlayer(player);
    }

    /**
     * Gives a QR code map to a player
     *
     * @param player the player to give the map to
     * @param url the URL where the map will point
     */
    public void giveMapToPlayer(Player player, String url) {
        World world = Bukkit.getWorlds().get(0);

        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            try {
                URL link = new URL(url);
                BufferedImage image = ImageIO.read(link);
                QRCodeRenderer renderer = new QRCodeRenderer(image);
                MapView map = Bukkit.createMap(world);
                map.setScale(MapView.Scale.FARTHEST);
                map.getRenderers().forEach(map::removeRenderer); // Remove any default renderers
                map.addRenderer(renderer); // Add our renderer
                short id = map.getId();
                getServer().getScheduler().runTask(this, () -> {
                    if (player.isOnline()) {
                        ItemStack mapItem = new ItemStack(Material.MAP, 1, id);
                        ItemMeta meta = mapItem.getItemMeta();
                        meta.setDisplayName(QR_CODE_MAP_NAME);
                        mapItem.setItemMeta(meta);
                        player.getInventory().addItem(mapItem);
                    }
                });
            } catch (IOException e) {
                getLogger().log(Level.SEVERE, "Failed to give map object", e);
            }
        });
    }

    /**
     * Removes all QR Code maps from a players inventory
     *
     * @param player the player
     */
    public void removeMapsFromPlayer(Player player) {
        for (int i = 0 ; i < player.getInventory().getSize() ; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (isQRCodeMap(item)) {
                player.getInventory().setItem(i, null);
            }
        }
    }

    /**
     * Returns whether an {@link ItemStack} is a QR Code map
     *
     * @param item the item to check
     * @return true if the item is a QR Code map
     */
    public boolean isQRCodeMap(ItemStack item) {
        return item != null && item.getType() == Material.MAP
                && item.hasItemMeta() && item.getItemMeta().hasDisplayName()
                && item.getItemMeta().getDisplayName().equals(QR_CODE_MAP_NAME);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();

        if (subchannel.equals("AuthMapGive")) {
            UUID uuid = UUID.fromString(in.readUTF());
            String url = in.readUTF();
            Player target = Bukkit.getPlayer(uuid);
            giveMapToPlayer(target, url);
        } else if (subchannel.equals("AuthMapRemove")) {
            UUID uuid = UUID.fromString(in.readUTF());
            Player target = Bukkit.getPlayer(uuid);
            removeMapsFromPlayer(target);
        }
    }
}
