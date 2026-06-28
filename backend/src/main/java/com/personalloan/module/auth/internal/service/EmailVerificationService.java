package com.personalloan.module.auth.internal.service;

import com.personalloan.common.exception.BusinessException;
import com.personalloan.common.exception.ResourceNotFoundException;
import com.personalloan.common.notification.EmailService;
import com.personalloan.common.util.HashUtils;
import com.personalloan.common.util.SecureTokenGenerator;
import com.personalloan.module.auth.internal.entity.User;
import com.personalloan.module.auth.internal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    /**
     * Generates a new secure verification token, saves its SHA-256 hash in the database,
     * and sends the raw token activation link to the user's email address.
     *
     * @param user the user to verify
     */
    @Transactional
    public void sendVerificationEmail(User user) {
        String rawToken = SecureTokenGenerator.generateToken();
        String hashedToken = HashUtils.sha256Hex(rawToken);

        user.setEmailVerificationToken(hashedToken);
        user.setEmailVerificationExpiry(java.time.LocalDateTime.now().plusHours(24));
        userRepository.save(user);

        String activationLink = "http://localhost:5173/verify-email?token=" + rawToken;
        String subject = "Activate Your Account - Personal Loan System";
        String body = String.format(
                "Hello,\n\nThank you for registering. Please activate your account by clicking the link below:\n%s\n\nNote: This link is valid for 24 hours only.\n\nBest regards,\nPersonal Loan Team",
                activationLink
        );

        emailService.sendEmail(user.getEmail(), subject, body);
        log.info("Verification email sent to user: {}", user.getEmail());
    }

    /**
     * Validates the verification token, activates the user, and invalidates the token.
     *
     * @param rawToken the raw token received from the user
     * @throws ResourceNotFoundException if the token is invalid or does not match any user
     * @throws BusinessException if the user is already active or if the link has expired
     */
    @Transactional
    public void verifyEmail(String rawToken) {
        if (rawToken == null || rawToken.trim().isEmpty()) {
            throw new BusinessException("Verification token cannot be null or empty");
        }

        String hashedToken = HashUtils.sha256Hex(rawToken);
        User user = userRepository.findByEmailVerificationToken(hashedToken)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid email verification token"));

        if ("ACTIVE".equals(user.getStatus())) {
            throw new BusinessException("User account is already active");
        }

        if (user.getEmailVerificationExpiry() == null || user.getEmailVerificationExpiry().isBefore(java.time.LocalDateTime.now())) {
            // Revoke expired token fields
            user.setEmailVerificationToken(null);
            user.setEmailVerificationExpiry(null);
            userRepository.save(user);
            throw new BusinessException("Email verification link has expired. Please register again.");
        }

        user.setStatus("ACTIVE");
        user.setEmailVerificationToken(null);
        user.setEmailVerificationExpiry(null);
        userRepository.save(user);

        log.info("User verified and activated successfully: {}", user.getEmail());
    }
}
