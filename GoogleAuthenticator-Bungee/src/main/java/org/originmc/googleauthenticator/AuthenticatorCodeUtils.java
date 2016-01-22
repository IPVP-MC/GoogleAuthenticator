package org.originmc.googleauthenticator;

import org.apache.commons.codec.binary.Base32;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A Google Authenticator utility class
 * <p>
 * src: https://code.google.com/p/vellum/wiki/GoogleAuthenticator
 */
public final class AuthenticatorCodeUtils {

    private final static Random numberGenerator = new SecureRandom();
    private final static Base32 base32 = new Base32();

    /**
     * Generates a random secret for the Google Authenticator client
     *
     * @return a 10 byte random number encoded using {@link Base32}
     */
    public static String generateNewSecret() {
        byte[] buffer = new byte[10];
        numberGenerator.nextBytes(buffer);
        return new String(base32.encode(buffer));
    }

    /**
     * Returns the number of 30s intervals since the UNIX time epoch
     *
     * @return the time index
     */
    public static long getTimeIndex() {
        return System.currentTimeMillis() / 1000 / 30;
    }

    /**
     * Returns whether or not a code matches a secret at a given time index
     *
     * @param secret the users secret
     * @param code the code to check
     * @param timeIndex the time index
     * @param variance
     * @return
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     */
    public static boolean verifyCode(String secret, int code, long timeIndex, int variance)
            throws InvalidKeyException, NoSuchAlgorithmException {
        byte[] secretBytes = base32.decode(secret);
        for (int i = -variance ; i <= variance ; i++) {
            if (getCode(secretBytes, timeIndex + i) == code) {
                return true;
            }
        }
        return false;
    }

    // Gets the code for a secret at a given time index
    private static long getCode(String secret, long timeIndex)
            throws NoSuchAlgorithmException, InvalidKeyException {
        return getCode(base32.decode(secret), timeIndex);
    }

    // Gets a list of valid codes at a time index with a given bound for time index
    private static List<Long> getCodeList(String secret, long timeIndex, int variance)
            throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] secretBytes = base32.decode(secret);
        List<Long> list = new ArrayList<>();
        for (int i = -variance ; i <= variance ; i++) {
            list.add(getCode(secretBytes, timeIndex + i));
        }
        return list;
    }

    // Gets the code for a secret at a given time index
    private static long getCode(byte[] secret, long timeIndex)
            throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec signKey = new SecretKeySpec(secret, "HmacSHA1");
        ByteBuffer buffer = ByteBuffer.allocate(8); // Allocate an 8 byte array for the time index
        buffer.putLong(timeIndex); // Input the time index into the array
        byte[] timeBytes = buffer.array(); // Get the byte array for time bytes
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signKey);
        byte[] hash = mac.doFinal(timeBytes); // Use HmacSHA1 to hash the time bytes array into 20 bytes

        // Take the first nibble (4 bits) of the last byte to use as an offset in the 20 byte array
        int offset = hash[19] & 0xf;
        long truncatedHash = hash[offset] & 0x7f;

        // Extract four bytes from from the offset and zero the highest order bit
        for (int i = 1 ; i < 4 ; i++) {
            truncatedHash <<= 8;
            truncatedHash |= hash[offset + i] & 0xff;
        }

        // Return the lower 6 decimal digits - this is our password
        return truncatedHash % 1000000;
    }

    /**
     * Fetches a QR barcode URL using the Google Chart API service which users can scan into
     * their Google Authenticator application
     *
     * @param user   the name of the user
     * @param host   the host name
     * @param secret the users secret code
     * @return the QR barcode URL
     */
    public static String getQRBarcodeURL(String user, String host, String secret) {
        return "http://chart.googleapis.com/chart?" + getQRBarcodeURLQuery(user, host, secret);
    }

    /*
     * Returns the URL query
     */
    private static String getQRBarcodeURLQuery(String user, String host, String secret) {
        try {
            return "chs=128x128&chld=M%7C0&cht=qr&chl=" + URLEncoder.encode(getQRBarcodeOtpAuthURL(user, host, secret), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the QR Barcode OTP Auth URL for a user and host
     *
     * @param user   the name of the user
     * @param host   the host name
     * @param secret the users secret code
     * @return the QR barcode OTP Auth URL
     */
    public static String getQRBarcodeOtpAuthURL(String user, String host, String secret) {
        return String.format("otpauth://totp/%s@%s?secret=%s", user, host, secret);
    }

    private AuthenticatorCodeUtils() {
        throw new IllegalAccessError("access to constructor is forbidden");
    }
}
