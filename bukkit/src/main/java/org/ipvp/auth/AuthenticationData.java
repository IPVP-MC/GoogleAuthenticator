package org.ipvp.auth;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A thread safe data class that holds authentication information for a Player
 */
public final class AuthenticationData {

    private final String secret;
    private volatile String ip;
    private AtomicBoolean ipTrusted;
    private AtomicBoolean authenticated = new AtomicBoolean(false);

    /**
     * Creates a data object holding a players Google Authenticator secret code and last authenticated IP address
     *
     * @param secret the secret code for the player
     * @param ip     the last authenticated IP address of the player
     */
    public AuthenticationData(String secret, String ip) {
        this(secret, ip, false);
    }

    /**
     * Creates a data object holding a players Google Authenticator secret code and last authenticated IP address
     *
     * @param secret  the secret code for the player
     * @param ip      the last authenticated IP address of the player
     * @param ipTrust whether or not the players IP address should be trusted
     */
    public AuthenticationData(String secret, String ip, boolean ipTrust) {
        this.secret = secret;
        this.ip = ip;
        this.ipTrusted = new AtomicBoolean(ipTrust);
    }

    /**
     * Returns the secret code for the player
     *
     * @return the players secret code
     */
    public String getSecret() {
        return secret;
    }

    /**
     * Returns the last authenticated IP address for the player
     *
     * @return the last authenticated IP address
     */
    public String getIp() {
        return ip;
    }

    /**
     * Sets the authenticated IP address for the player
     *
     * @param ip the most recently authenticated IP address
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * Returns whether or not the player trusts the IP
     *
     * @return true if the player trusts the IP
     */
    public boolean isTrustingIp() {
        return ipTrusted.get();
    }

    /**
     * Sets whether or not the IP of the player should be trusted
     *
     * @param ipTrusted the IP of the player
     */
    public void setIpTrusted(boolean ipTrusted) {
        this.ipTrusted.set(ipTrusted);
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
