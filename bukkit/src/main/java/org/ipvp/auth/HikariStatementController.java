package org.ipvp.auth;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

public class HikariStatementController {

    private AuthPlugin plugin;
    private HikariDataSource hikariDataSource;

    /**
     * Instantiates our database controller
     */
    HikariStatementController(AuthPlugin plugin) throws SQLException {
        this.plugin = plugin;

        HikariConfig config = new HikariDataSource();
        config.setJdbcUrl(plugin.getConfig().getString("database.url"));
        config.setUsername(plugin.getConfig().getString("database.username"));
        config.setPassword(plugin.getConfig().getString("database.password"));

        // Configure the Hikari connection pool.
        config.setConnectionTimeout(plugin.getConfig().getLong("database.pool.connection-timeout"));
        config.setIdleTimeout(plugin.getConfig().getLong("database.pool.idle-timeout"));
        config.setMaxLifetime(plugin.getConfig().getLong("database.pool.max-lifetime"));
        config.setMinimumIdle(plugin.getConfig().getInt("database.pool.minimum-idle"));
        config.setMaximumPoolSize(plugin.getConfig().getInt("database.pool.maximum-pool-size"));
        config.setThreadFactory(new ThreadFactoryBuilder().setDaemon(true)
                .setNameFormat("hikari-sql-pool-%d").build());

        this.hikariDataSource = new HikariDataSource(config);

        // Test the connection
        Connection connection = this.hikariDataSource.getConnection();
        connection.close();

        // Attempt to create the table
        this.createDatabaseTable();
    }

    /*
     * A helper method that creates the auth table in the database
     */
    private void createDatabaseTable() {
        Connection connection = getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `auth_players` (" +
                    "`UUID` VARCHAR(40) NOT NULL," + // The UUID of the user
                    "`SECRET` VARCHAR(36)," + // The users secret code
                    "`IP` TINYTEXT NOT NULL," + // The IP column represents the last authenticated IP for the user
                    "`TRUST_IP` BIT(1) NOT NULL," + // Whether or not the player has their IP saved as a trusted IP
                    "PRIMARY KEY (`UUID`))");
            statement.executeUpdate();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to create the auth_players table in the database");
        }
    }

    /**
     * Closes the HikariCP data source
     */
    void closeDataSource() {
        this.hikariDataSource.close();
    }

    /**
     * Attempts to get a database connection. If anything fails
     * the method returns null and prints a stack error in the
     * console.
     *
     * @return a database connection
     */
    public Connection getConnection() {
        try {
            return this.hikariDataSource.getConnection();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get database connection", e);
            return null;
        }
    }

    /**
     * Fetches auth data from MySQL database for a specific Player. If
     * the {@link UUID} of the player is not in the database this method will return null.
     *
     * @param uuid the {@link UUID} of the Player
     * @return the AuthenticationData for the player
     */
    public AuthenticationData getAuthenticationData(UUID uuid) {
        Connection connection = getConnection();
        AuthenticationData data = null; // default to null value
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `auth_players` WHERE UUID=? LIMIT 1");
            statement.setString(1, uuid.toString());
            ResultSet set = statement.executeQuery();

            // Get the authentication data of the player
            if (set.next()) {
                data = new AuthenticationData(
                        set.getString("SECRET"),
                        set.getString("IP"),
                        set.getBoolean("TRUST_IP")
                );
            }

            // Close anything connection related
            set.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "There was an error getting the authentication data of UUID "
                    + uuid.toString(), e);
        }

        return data;
    }

    /**
     * Updates a Player authentication data in the MySQL database
     *
     * @param uuid the {@link UUID} of the player
     * @param data the players {@link AuthenticationData}
     */
    public void updateAuthenticationData(UUID uuid, AuthenticationData data) {
        Connection connection = getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO `auth_players` " +
                    "(UUID, SECRET, IP, TRUST_IP) VALUES(?, ?, ?, ?)" +
                    "ON DUPLICATE KEY UPDATE " +
                    "SECRET=VALUES(SECRET), " +
                    "IP=VALUES(IP), " +
                    "TRUST_IP=VALUES(TRUST_IP)");
            statement.setString(1, uuid.toString());
            statement.setString(2, data.getSecret());
            statement.setString(3, data.getIp());
            statement.setBoolean(4, data.isTrustingIp());
            statement.executeUpdate();

            // Close anything connection related
            statement.close();
            connection.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "There was an error updating data for UUID " + uuid.toString(), e);
        }
    }

    /**
     * Removes the {@link AuthenticationData} of a Player
     *
     * @param uuid the {@link UUID} of the player
     */
    public void removeAuthenticationData(UUID uuid) {
        Connection connection = getConnection();

        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM `auth_players` " +
                    "WHERE " +
                    "UUID=?");
            statement.setString(1, uuid.toString());
            statement.executeUpdate();

            // Close anything connection related
            statement.close();
            connection.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "There was an error updating data for UUID " + uuid.toString(), e);
        }
    }
}
