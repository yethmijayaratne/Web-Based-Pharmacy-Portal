package com.example.pharmacy_System.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordUtil {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Encrypt password using BCrypt
    public static String encrypt(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }

    // Check if plain password matches encrypted password
    public static boolean checkPassword(String plainPassword, String encryptedPassword) {
        return passwordEncoder.matches(plainPassword, encryptedPassword);
    }

    // For backward compatibility - decrypt is not possible with BCrypt
    public static String decrypt(String encryptedPassword) {
        throw new UnsupportedOperationException("BCrypt passwords cannot be decrypted");
    }
}