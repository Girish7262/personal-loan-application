package com.personalloan.module.auth.service;

import com.personalloan.common.exception.UnauthorizedException;
import com.personalloan.common.util.HashUtils;
import com.personalloan.common.util.SecureTokenGenerator;
import com.personalloan.module.auth.entity.User;
import com.personalloan.module.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final UserRepository userRepository;

    /**
     * Generates a secure raw refresh token, hashes it using SHA-256, and stores it in the database
     * on the user record with a 7-day expiration timestamp.
     *
     * @param user the user requesting session creation
     * @return the raw URL-safe refresh token string
     */
    @Transactional
    public String createRefreshToken(User user) {
        String rawToken = SecureTokenGenerator.generateToken();
        String hashedToken = HashUtils.sha256Hex(rawToken);

        user.setRefreshTokenHash(hashedToken);
        user.setRefreshTokenExpiry(LocalDateTime.now().plusDays(7));
        userRepository.save(user);

        log.info("Secure refresh token created and hashed for user: {}", user.getEmail());
        return rawToken;
    }

    /**
     * Finds the user record associated with the raw refresh token hash.
     *
     * @param rawRefreshToken the raw refresh token received from client
     * @return the matching User entity
     * @throws UnauthorizedException if no user matches the token hash
     */
    @Transactional(readOnly = true)
    public User resolveUserFromToken(String rawRefreshToken) {
        if (rawRefreshToken == null || rawRefreshToken.trim().isEmpty()) {
            throw new UnauthorizedException("Refresh token is required");
        }

        String hashedToken = HashUtils.sha256Hex(rawRefreshToken);
        return userRepository.findByRefreshTokenHash(hashedToken)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));
    }

    /**
     * Validates the refresh token and rotates it by generating a new secure token,
     * hashing it, updating the user's record, and invalidating the old token hash.
     *
     * @param user the user whose token is rotated
     * @param rawRefreshToken the old raw token string
     * @return the new raw URL-safe refresh token string
     * @throws UnauthorizedException if the token matches but has expired or is invalid
     */
    @Transactional
    public String rotateRefreshToken(User user, String rawRefreshToken) {
        String hashedToken = HashUtils.sha256Hex(rawRefreshToken);

        if (user.getRefreshTokenHash() == null || !user.getRefreshTokenHash().equals(hashedToken)) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        if (user.getRefreshTokenExpiry() == null || user.getRefreshTokenExpiry().isBefore(LocalDateTime.now())) {
            // Revoke immediately on verification failure
            user.setRefreshTokenHash(null);
            user.setRefreshTokenExpiry(null);
            userRepository.save(user);
            throw new UnauthorizedException("Refresh token has expired");
        }

        // Generate new raw refresh token and its hash
        String newRawToken = SecureTokenGenerator.generateToken();
        String newHashedToken = HashUtils.sha256Hex(newRawToken);

        // Rotate: update columns in database, automatically invalidating old hash
        user.setRefreshTokenHash(newHashedToken);
        user.setRefreshTokenExpiry(LocalDateTime.now().plusDays(7));
        userRepository.save(user);

        log.info("Refresh token rotated successfully for user: {}", user.getEmail());
        return newRawToken;
    }

    /**
     * Invalidates the active refresh token hash for a user (e.g. during logout).
     *
     * @param user the user to log out
     */
    @Transactional
    public void invalidateToken(User user) {
        user.setRefreshTokenHash(null);
        user.setRefreshTokenExpiry(null);
        userRepository.save(user);
        log.info("Invalidated refresh token for user: {}", user.getEmail());
    }
}
