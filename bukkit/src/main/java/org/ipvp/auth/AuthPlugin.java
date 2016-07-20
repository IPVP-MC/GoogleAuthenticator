package org.ipvp.auth;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.ipvp.auth.commands.AuthCommand;
import org.ipvp.auth.listeners.PlayerListener;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class AuthPlugin extends JavaPlugin implements PluginMessageListener {

    public static final String QR_CODE_MAP_NAME = ChatColor.GOLD + "Google Authenticator Map";
    
    private HikariStatementController hikariController;
    private Map<UUID, AuthenticationData> playerAuthenticationData = new ConcurrentHashMap<>();
    private PlayerListener playerListener;
    // Active conversations
    private Map<UUID, Conversation> conversations = new ConcurrentHashMap<>();
    
    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(playerListener = new PlayerListener(this), this);
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getCommand("auth").setExecutor(new AuthCommand(this));
        try { // Try to initialize HikariCP
            hikariController = new HikariStatementController(this);
        } catch (SQLException e) {
            getLogger().log(Level.SEVERE, "failed to initialize hikari controller", e);
        }

        // Incase of reload, load all online proxy players
        getServer().getOnlinePlayers().forEach(player -> {
            //player.sendMessage(ChatColor.GREEN + "Due to a server reload, you may need to re-authenticate.");
            playerListener.onPlayerConnect(new AsyncPlayerPreLoginEvent(player.getName(), player.getAddress().getAddress(), player.getUniqueId()));
            playerListener.onPlayerConnect(new PlayerJoinEvent(player, null));
        });
    }

    @Override
    public void onDisable() {
        conversations.keySet().forEach(this::removeConversation);
        playerAuthenticationData.keySet().forEach(this::clearAndUpdateAuthenticationData); // Save all data when disabling
        hikariController.closeDataSource();
    }

    /**
     * Returns the Hikari MySQL Database Controller
     *
     * @return the hikari DB controller
     */
    public HikariStatementController getController() {
        return hikariController;
    }


    /**
     * Adds authentication data for a Player
     *
     * @param uuid the {@link UUID} of the player
     * @param data the auth data of the player
     */
    public void addAuthenticationData(UUID uuid, AuthenticationData data) {
        playerAuthenticationData.put(uuid, data);
    }

    /**
     * Removes and updates a Player {@link AuthenticationData} in the
     * MySQL database
     *
     * @param uuid the {@link UUID} of the player
     */
    public void clearAndUpdateAuthenticationData(UUID uuid) {
        AuthenticationData data = playerAuthenticationData.remove(uuid);

        if (data != null) {
            getController().updateAuthenticationData(uuid, data);
        }
    }

    /**
     * Removes the {@link AuthenticationData} of a Player and
     * updates the MySQL database to reflect the changes
     *
     * @param uuid the {@link UUID} of the player
     */
    public void removeAuthenticationData(UUID uuid) {
        playerAuthenticationData.remove(uuid);
        getController().removeAuthenticationData(uuid);
    }

    /**
     * Gets the {@link AuthenticationData} of a Player
     *
     * @param uuid the {@link UUID} of the Player
     * @return the auth data of the player
     */
    public AuthenticationData getAuthenticationData(UUID uuid) {
        return playerAuthenticationData.get(uuid);
    }

    /**
     * Returns whether or not a Player has any
     * stored {@link AuthenticationData}
     *
     * @param uuid the {@link UUID} of the Player
     * @return true if the player has any auth data stored
     */
    public boolean hasAuthenticationData(UUID uuid) {
        return playerAuthenticationData.containsKey(uuid);
    }
    
    public void registerConversation(UUID uuid, Conversation conversation) {
        conversations.put(uuid, conversation);
    }

    public void removeConversation(UUID uuid) {
        Conversation conversation = conversations.remove(uuid);
        if (conversation != null) {
            conversation.abandon();
        }
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

        ByteArrayDataInput input = ByteStreams.newDataInput(message);
        String sub = input.readUTF();

        if (!sub.equals("PlayerLogout")) {
            return;
        }

        UUID uuid = UUID.fromString(input.readUTF());
        clearAndUpdateAuthenticationData(uuid);
    }
    
    public void sendBungeeAuthentication(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Authenticated");
        out.writeUTF(player.getUniqueId().toString());
        player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
    }
}
