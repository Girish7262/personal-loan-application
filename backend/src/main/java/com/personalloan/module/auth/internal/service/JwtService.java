package com.personalloan.module.auth.internal.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import com.personalloan.module.auth.internal.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${app.jwt.secret:Y2hhbmdlLW1lLWluLXByb2R1Y3Rpb24tdXNlLWF0LWxlYXN0LTI1Ni1iaXQtc2VjcmV0LWtleS1hYmNkZWZnaGlqa2xtbm9wcXJzdHV2d3h5eg==}")
    private String jwtSecret;

    @Value("${app.jwt.access-token-expiration-ms:900000}") // Default 15 minutes (900,000 ms)
    private Long accessExpirationMs;

    @jakarta.annotation.PostConstruct
    public void init() {
        if (jwtSecret == null || jwtSecret.trim().isEmpty()) {
            throw new IllegalStateException("JWT Secret key is not configured! Please configure JWT_SECRET environment variable or activate the dev profile.");
        }
    }

    /**
     * Generates a signed JWT access token for a user containing sub, role, userId, and jti claims.
     *
     * @param user the user details to generate token for
     * @return the signed access token string
     */
    public String generateAccessToken(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", user.getRole().getRoleCode());
        extraClaims.put("userId", user.getUserId());
        extraClaims.put("jti", UUID.randomUUID().toString()); // Token ID for future blacklisting/audit
        return buildToken(extraClaims, user.getEmail(), accessExpirationMs);
    }

    private String buildToken(Map<String, Object> extraClaims, String subject, long expirationMs) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Validates a JWT access token against the user's email identity and checking signature/expiration.
     *
     * @param token the access token
     * @param email the loaded user's email
     * @return true if valid and not expired, false otherwise
     */
    public boolean isTokenValid(String token, String email) {
        final String username = extractUsername(token);
        return (username.equals(email)) && !isTokenExpired(token);
    }

    /**
     * Extracts the subject (email) from the token claims.
     *
     * @param token the access token
     * @return the extracted email string
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim using a custom resolver function.
     *
     * @param token the access token
     * @param claimsResolver claims resolver function
     * @return the resolved claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(jwtSecret);
        } catch (IllegalArgumentException ex) {
            // Fall back to plain bytes of secret if base64 decoding fails in development
            keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Gets the expiration TTL of access tokens in seconds.
     *
     * @return token expiration in seconds
     */
    public Long getAccessExpirationSeconds() {
        return accessExpirationMs / 1000;
    }
}
