package com.personalloan.common.util;

import java.security.SecureRandom;
import java.util.Base64;

public final class SecureTokenGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final int TOKEN_BYTE_LENGTH = 32;

    private SecureTokenGenerator() {
        // Prevent instantiation of utility class
    }

    /**
     * Generates a cryptographically secure 32-byte random token
     * encoded in Base64 URL-safe format without padding.
     *
     * @return a secure random token string
     */
    public static String generateToken() {
        byte[] randomBytes = new byte[TOKEN_BYTE_LENGTH];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
