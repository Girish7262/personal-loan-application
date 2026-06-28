package com.personalloan.module.auth.internal.service;

import com.personalloan.common.event.AuditEvent;
import com.personalloan.common.exception.BusinessException;
import com.personalloan.common.exception.UnauthorizedException;
import com.personalloan.module.auth.api.dto.AuthResponse;
import com.personalloan.module.auth.api.dto.ChangePasswordRequest;
import com.personalloan.module.auth.api.dto.LoginRequest;
import com.personalloan.module.auth.api.dto.RegisterRequest;
import com.personalloan.module.auth.internal.entity.User;
import com.personalloan.module.auth.internal.repository.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final EmailVerificationService emailVerificationService;
    private final PasswordPolicyService passwordPolicyService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordHistoryService passwordHistoryService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Registers a new customer in the system. Enforces rate limits (10 requests/hour/IP),
     * password strength guidelines, database unique constraints, and sends verification email.
     *
     * @param request the registration details
     */
    @Transactional
    public void register(RegisterRequest request) {
        passwordPolicyService.validatePassword(request.getPassword(), request.getConfirmPassword());

        User user = userService.createUser(
                request.getEmail(),
                request.getPassword(),
                request.getMobileNumber(),
                "CUSTOMER"
        );

        emailVerificationService.sendVerificationEmail(user);
        log.info("Customer registration successful for: {}", request.getEmail());
    }

    /**
     * Authenticates a user. Validates rate limits (5 attempts/min/IP), account status,
     * lockouts, and credentials.
     * Note: This method is intentionally NOT marked @Transactional to prevent holding
     * database connections open during slow BCrypt operations (~100ms per attempt).
     *
     * @param request the login credentials
     * @return the authentication response including secure tokens
     * @throws UnauthorizedException for invalid credentials or locked/inactive accounts
     */
    public AuthResponse login(LoginRequest request) {
        HttpServletRequest currentRequest = getCurrentRequest();
        String clientIp = getClientIp(currentRequest);
        String userAgent = getUserAgent(currentRequest);

        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            // Audit failed login attempt (no user matching email)
            saveAuditLog(null, "LOGIN", "FAILURE", clientIp, userAgent);
            throw new UnauthorizedException("Invalid email or password");
        }

        User user = userOpt.get();

        // 1. Check account lockout state
        if (user.getLockoutUntil() != null) {
            if (user.getLockoutUntil().isAfter(LocalDateTime.now())) {
                saveAuditLog(user, "LOGIN", "FAILURE_LOCKED", clientIp, userAgent);
                throw new UnauthorizedException("Your account has been locked due to excessive failed attempts. Please try again later.");
            } else {
                // Lockout period has passed, reset state
                userService.resetFailedLoginAttempts(user);
            }
        }

        // 2. Check if account is suspended or locked by admin
        if ("LOCKED".equals(user.getStatus()) || "SUSPENDED".equals(user.getStatus())) {
            saveAuditLog(user, "LOGIN", "FAILURE_SUSPENDED", clientIp, userAgent);
            throw new UnauthorizedException("Your account has been locked or suspended. Please contact support.");
        }

        // 3. Check if email is verified
        if ("INACTIVE".equals(user.getStatus())) {
            saveAuditLog(user, "LOGIN", "FAILURE_UNVERIFIED", clientIp, userAgent);
            throw new UnauthorizedException("Please verify your email before logging in.");
        }

        // 4. Verify credentials
        if (passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            // Audits successful login and resets failures (runs in its own transactional context)
            userService.recordSuccessfulLogin(user);

            // Fetch secure tokens from dedicated token services
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = refreshTokenService.createRefreshToken(user);

            saveAuditLog(user, "LOGIN", "SUCCESS", clientIp, userAgent);
            
            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtService.getAccessExpirationSeconds())
                    .role(user.getRole().getRoleCode())
                    .forcePasswordChange(user.getForcePasswordChange())
                    .build();
        } else {
            // Register failed attempt and trigger lockout if threshold reached (transactional)
            userService.handleFailedLogin(user);
            saveAuditLog(user, "LOGIN", "FAILURE", clientIp, userAgent);
            throw new UnauthorizedException("Invalid email or password");
        }
    }

    /**
     * Refreshes user session tokens using a valid, active refresh token. Rotates the credentials
     * by invalidating the old token hash and generating a new raw refresh token and access token.
     *
     * @param rawRefreshToken the raw refresh token received from client
     * @return the authentication response including secure tokens
     */
    @Transactional
    public AuthResponse refreshToken(String rawRefreshToken) {
        User user = refreshTokenService.resolveUserFromToken(rawRefreshToken);
        String newRefreshToken = refreshTokenService.rotateRefreshToken(user, rawRefreshToken);
        String newAccessToken = jwtService.generateAccessToken(user);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessExpirationSeconds())
                .role(user.getRole().getRoleCode())
                .forcePasswordChange(user.getForcePasswordChange())
                .build();
    }

    /**
     * Changes a user's password. Validates that the old password is correct, that the new password
     * matches policy guidelines and has not been used in the user's last 5 passwords, and saves it.
     *
     * @param currentUser the authenticated user executing the request
     * @param request the password change payload
     */
    @Transactional
    public void changePassword(User currentUser, ChangePasswordRequest request) {
        if (!passwordEncoder.matches(request.getOldPassword(), currentUser.getPasswordHash())) {
            throw new UnauthorizedException("Invalid old password");
        }

        passwordPolicyService.validatePassword(request.getNewPassword(), request.getConfirmPassword());

        // Validate password history checks (no reuse of last 5 passwords)
        passwordHistoryService.validatePasswordNotUsed(currentUser, request.getNewPassword());

        // Record the current password hash in history before updating it
        passwordHistoryService.recordPasswordChange(currentUser, currentUser.getPasswordHash());

        currentUser.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        currentUser.setForcePasswordChange(false);
        userRepository.save(currentUser);

        log.info("Password changed successfully for user: {}", currentUser.getEmail());
    }

    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    private String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "UNKNOWN";
        }
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String getUserAgent(HttpServletRequest request) {
        if (request == null) {
            return "UNKNOWN";
        }
        return request.getHeader("User-Agent");
    }

    private void saveAuditLog(User user, String action, String result, String ip, String userAgent) {
        eventPublisher.publishEvent(new AuditEvent(this, user != null ? user.getUserId() : null, action, result, ip, userAgent));
    }
}
