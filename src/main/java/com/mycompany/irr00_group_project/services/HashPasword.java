package com.mycompany.irr00_group_project.services;

import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * This class provides methods to hash passwords using PBKDF2 with HMAC SHA-256.
 * It also includes a method to generate a secure random salt.
 */
public class HashPasword {

    private static HashPasword instance;
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;

    /**
     * Hashes a password using PBKDF2 with HMAC SHA-256.
     *
     * @param password The password to hash.
     * @param salt     The salt to use for hashing.
     * @return The hashed password as a Base64 encoded string.
     */
    public static String hashPassword(String password, byte[] salt) {
        try {
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error while hashing password", e);
        }
    }

    /**
     * Generates a secure random salt.
     * @return A byte array containing the generated salt.
     */
    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16]; // 128-bit salt
        random.nextBytes(salt);
        return salt;
    }

    /**
     * Gets the singleton instance of HashPasword.
     * @return The singleton instance of HashPasword.
     */
    public static HashPasword getInstance() {
        if (instance == null) {
            instance = new HashPasword();
        }
        return instance;
    }
}
