package org.originmc.googleauthenticator;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A data class that holds authentication information for a {@link net.md_5.bungee.api.connection.ProxiedPlayer}
 */
public final class AuthenticationData {

    private final String secret;
    private final String ip;
    private AtomicBoolean authenticated = new AtomicBoolean(false);

    /**
     * Creates a data object holding a players Google Authenticator secret code and last authenticated IP address
     *
     * @param secret the secret code for the player
     * @param ip     the last authenticated IP address of the player
     */
    public AuthenticationData(String secret, String ip) {
        // TODO: Not-null checks (?)
        this.secret = secret;
        this.ip = ip;
    }

    /**
     * Returns the secret code for the {@link net.md_5.bungee.api.connection.ProxiedPlayer}
     *
     * @return the players secret code
     */
    public String getSecret() {
        return secret;
    }

    /**
     * Returns the last authenticated IP address for the {@link net.md_5.bungee.api.connection.ProxiedPlayer}
     *
     * @return the last authenticated IP address
     */
    public String getIp() {
        return ip;
    }

    /**
     * Returns the current authentication status of this data
     *
     * @return the current status
     */
    public boolean isAuthenticated() {
        return authenticated.get();
    }

    /**
     * Sets the authentication status of this data
     *
     * @param authenticated the new authentication status
     */
    public void setAuthenticated(boolean authenticated) {
        this.authenticated.set(authenticated);
    }
}
