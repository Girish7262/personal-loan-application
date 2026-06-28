package com.personalloan.module.auth.service;

import com.personalloan.common.exception.BusinessException;
import com.personalloan.common.exception.ResourceNotFoundException;
import com.personalloan.common.notification.EmailService;
import com.personalloan.common.util.HashUtils;
import com.personalloan.common.util.SecureTokenGenerator;
import com.personalloan.module.auth.entity.User;
import com.personalloan.module.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.HttpServletRequest;
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
public class PasswordResetService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordPolicyService passwordPolicyService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordHistoryService passwordHistoryService;

    /**
     * Initiates the password reset workflow. Generates a secure token, hashes it for database
     * storage (overwriting/invalidating any previous reset token for this user), sets a 15-minute
     * expiry timestamp, and emails the reset link.
     * Note: Does not throw an exception if the email is not found, preventing email enumeration.
     *
     * @param email the email address requesting the reset
     */
    @Transactional
    public void initiatePasswordReset(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new BusinessException("Email address cannot be null or empty");
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String rawToken = SecureTokenGenerator.generateToken();
            String hashedToken = HashUtils.sha256Hex(rawToken);

            user.setPasswordResetToken(hashedToken);
            user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));
            userRepository.save(user);

            String resetLink = "http://localhost:5173/reset-password?token=" + rawToken;
            String subject = "Reset Your Password - Personal Loan System";
            String body = String.format(
                    "Hello,\n\nYou requested a password reset. Please click the link below to set a new password:\n%s\n\nNote: This link is valid for 15 minutes only.\n\nBest regards,\nPersonal Loan Team",
                    resetLink
            );

            emailService.sendEmail(user.getEmail(), subject, body);
            log.info("Password reset token generated and email sent to: {}", email);
        } else {
            // Log warning but return silently to mitigate email enumeration vulnerabilities
            log.warn("Password reset requested for unregistered email address: {}", email);
        }
    }

    /**
     * Resets a user's password using a valid reset token. Enforces password policy validation,
     * encodes the new password, clears the reset token/expiry to prevent reuse, and unlocks
     * the password change gate.
     *
     * @param rawToken the raw reset token received from the user
     * @param newPassword the new password input
     * @param confirmPassword the password confirmation input
     * @throws ResourceNotFoundException if the token hash is not found in the database
     * @throws BusinessException if the token has expired
     */
    @Transactional
    public void resetPassword(String rawToken, String newPassword, String confirmPassword) {
        if (rawToken == null || rawToken.trim().isEmpty()) {
            throw new BusinessException("Reset token cannot be null or empty");
        }

        passwordPolicyService.validatePassword(newPassword, confirmPassword);

        String hashedToken = HashUtils.sha256Hex(rawToken);
        User user = userRepository.findByPasswordResetToken(hashedToken)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired password reset token"));

        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Password reset token has expired");
        }

        // Validate password history checks (no reuse of last 5 passwords)
        passwordHistoryService.validatePasswordNotUsed(user, newPassword);

        // Record current password in history before updating it
        passwordHistoryService.recordPasswordChange(user, user.getPasswordHash());

        // Encrypt new password, clear reset token fields, and clear force change gate
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setResetTokenExpiry(null);
        user.setForcePasswordChange(false);
        user.setFailedLoginAttempts(0); // Reset failures on successful reset
        user.setLockoutUntil(null);
        
        userRepository.save(user);
        log.info("Password reset successfully completed for user: {}", user.getEmail());
    }
}
