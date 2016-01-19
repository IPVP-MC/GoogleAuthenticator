package org.originmc.googleauthenticator;

import org.jboss.aerogear.security.otp.api.Base32;

import java.security.SecureRandom;
import java.util.Random;

public class AuthenticatorCodeUtils {

    private static Random numberGenerator = new SecureRandom();

    /**
     * Generates a random secret for the Google Authenticator client
     *
     * @return a 10 byte random number encoded using {@link Base32}
     */
    public static String generateNewSecret() {
        byte[] buffer = new byte[10];
        numberGenerator.nextBytes(buffer);
        return Base32.encode(buffer);
    }

    private AuthenticatorCodeUtils() {
        throw new IllegalAccessError("access to constructor is forbidden");
    }
}
