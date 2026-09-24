package de.oliver.fancylib;

import java.security.SecureRandom;

public class IDGen {

    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = ALPHABET.length();
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generates a random ID of the specified length using all characters from the alphabet.
     * Minimum recommended length is 8 for up to 1 million unique IDs.
     */
    public static String generate(int length) {
        if (length <= 0) {
            return "";
        }

        byte[] randomBytes = new byte[length];
        RANDOM.nextBytes(randomBytes);

        char[] result = new char[length];
        for (int i = 0; i < length; i++) {
            // Convert signed byte to unsigned range [0..255]
            int index = randomBytes[i] & 0xFF;
            result[i] = ALPHABET.charAt(index % BASE);
        }

        return new String(result);
    }

}
