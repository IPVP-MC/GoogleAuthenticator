package org.originmc.googleauthenticator;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.originmc.googleauthenticator.listeners.PlayerListener;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class GoogleAuthenticatorPlugin extends Plugin {

    private Configuration config;
    private HikariStatementController hikariController;
    private Map<UUID, AuthenticationData> playerAuthenticationData = new ConcurrentHashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getProxy().getPluginManager().registerListener(this, new PlayerListener(this));
        try { // Try to initialize HikariCP
            hikariController = new HikariStatementController(this);
        } catch (SQLException e) {
            getLogger().log(Level.SEVERE, "failed to initialize hikari controller", e);
        }
    }

    @Override
    public void onDisable() {
        hikariController.closeDataSource();
    }

    /**
     * Returns the configuration file for the plugin
     *
     * @return the plugins configuration file
     */
    public Configuration getConfig() {
        return config;
    }

    /**
     * Returns the Hikari MySQL Database Controller
     *
     * @return the hikari DB controller
     */
    public HikariStatementController getDatabase() {
        return hikariController;
    }

    /**
     * Adds authentication data for a {@link net.md_5.bungee.api.connection.ProxiedPlayer}
     *
     * @param uuid the {@link UUID} of the player
     * @param data the auth data of the player
     */
    public void addAuthenticationData(UUID uuid, AuthenticationData data) {
        playerAuthenticationData.put(uuid, data);
    }

    /**
     * Removes and updates a {@link net.md_5.bungee.api.connection.ProxiedPlayer} {@link AuthenticationData} in the
     * MySQL database
     *
     * @param uuid the {@link UUID} of the player
     */
    public void clearAndUpdateAuthenticationData(UUID uuid) {
        AuthenticationData data = playerAuthenticationData.remove(uuid);

        if (data != null) {
            getDatabase().updateAuthenticationData(uuid, data);
        }
    }

    /**
     * Gets the {@link AuthenticationData} of a {@link net.md_5.bungee.api.connection.ProxiedPlayer}
     *
     * @param uuid the {@link UUID} of the {@link net.md_5.bungee.api.connection.ProxiedPlayer}
     * @return the auth data of the player
     */
    public AuthenticationData getAuthenticationData(UUID uuid) {
        return playerAuthenticationData.get(uuid);
    }

    /**
     * Saves the default config.yml file to disc if it exists
     */
    public void saveDefaultConfig() {
        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(getResourceAsStream("config.yml"));
            try {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
            } catch (IOException e) {
                getLogger().severe("Couldn't save configuration file to data folder.");
                e.printStackTrace();
            }
        } else {
            try {
                this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            } catch (IOException e) {
                getLogger().severe("Couldn't load configuration file.");
                e.printStackTrace();
            }
        }
    }
}
