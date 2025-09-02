package com.mycompany.irr00_group_project.services;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * Unit tests for HashPasword utilities and AuthenticationService's
 * private password / email validators.
 */
public class HashPasswordTest {

    @Test
    void testSaltGenerationProducesRandom16Bytes() {
        byte[] s1 = HashPasword.generateSalt();
        byte[] s2 = HashPasword.generateSalt();

        assertEquals(16, s1.length, "Salt should be 16 bytes");
        assertEquals(16, s2.length, "Salt should be 16 bytes");
        assertFalse(Arrays.equals(s1, s2), "Two generated salts should differ");
    }

    @Test
    void testHashDeterministicWithSameSalt() {
        byte[] salt = HashPasword.generateSalt();
        String h1 = HashPasword.hashPassword("secret123", salt);
        String h2 = HashPasword.hashPassword("secret123", salt);

        assertEquals(h1, h2, "Hashing same password+salt should be deterministic");
    }

    @Test
    void testHashChangesWithDifferentSalt() {
        byte[] s1 = HashPasword.generateSalt();
        byte[] s2 = HashPasword.generateSalt();
        String h1 = HashPasword.hashPassword("secret123", s1);
        String h2 = HashPasword.hashPassword("secret123", s2);

        assertNotEquals(h1, h2, "Different salts should yield different hashes");
    }
}
