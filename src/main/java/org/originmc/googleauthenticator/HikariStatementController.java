package org.originmc.googleauthenticator;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

public class HikariStatementController {

    private GoogleAuthenticatorPlugin plugin;
    private HikariDataSource hikariDataSource;

    /**
     * Instantiates our database controller
     */
    HikariStatementController(GoogleAuthenticatorPlugin plugin) throws SQLException {
        this.plugin = plugin;

        // Load HikariCP
        Properties props = new Properties();
        props.setProperty("dataSourceClassName", "com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        props.setProperty("dataSource.user", plugin.getConfig().getString("mysql.username"));
        props.setProperty("dataSource.password", plugin.getConfig().getString("mysql.password"));
        props.setProperty("dataSource.databaseName", plugin.getConfig().getString("mysql.db"));
        props.setProperty("dataSource.serverName", plugin.getConfig().getString("mysql.host"));
        props.setProperty("dataSource.portNumber", plugin.getConfig().getString("mysql.port"));

        // Configure Hikari data pool
        HikariConfig hikariConfig = new HikariConfig(props);
        hikariConfig.setLeakDetectionThreshold(10000L);
        hikariConfig.setMaxLifetime(25000L);
        hikariConfig.setIdleTimeout(20000L);
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setConnectionTimeout(10000L);
        hikariConfig.setInitializationFailFast(false);
        this.hikariDataSource = new HikariDataSource(hikariConfig);

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
                    "`SECRET` int(36)," + // The users secret code
                    "`IP` VARCHAR(15) NOT NULL," + // The IP column represents the last authenticated IP for the user
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
     * Returns the {@link HikariDataSource}
     *
     * @return the data source
     */
    public DataSource getSource() {
        return hikariDataSource;
    }
}
