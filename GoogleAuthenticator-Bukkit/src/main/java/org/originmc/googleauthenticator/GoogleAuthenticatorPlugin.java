package org.originmc.googleauthenticator;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
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

public class GoogleAuthenticatorPlugin extends JavaPlugin implements PluginMessageListener {

    @Override
    public void onEnable() {
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
    }

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
                    ItemStack mapItem = new ItemStack(Material.MAP, 1, id);
                    ItemMeta meta = mapItem.getItemMeta();
                    meta.setDisplayName(ChatColor.RED + "Destroy when done");
                    mapItem.setItemMeta(meta);
                    player.getInventory().addItem();
                });
            } catch (IOException e) {
                getLogger().log(Level.SEVERE, "Failed to give map object", e);
            }
        });
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
        }
    }
}
