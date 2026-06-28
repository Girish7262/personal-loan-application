package com.personalloan.module.auth.internal.service;

import com.personalloan.common.exception.BusinessException;
import com.personalloan.common.exception.DuplicateResourceException;
import com.personalloan.common.exception.ResourceNotFoundException;
import com.personalloan.module.auth.internal.entity.Role;
import com.personalloan.module.auth.internal.entity.User;
import com.personalloan.module.auth.internal.repository.RoleRepository;
import com.personalloan.module.auth.internal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates a new user record in the database. Assigns the requested role, hashes the password
     * using BCrypt, and sets default audit and status parameters.
     *
     * @param email the user email address
     * @param password the plain text password
     * @param mobileNumber the mobile number
     * @param roleCode the target system role code (e.g. CUSTOMER)
     * @return the created User entity
     * @throws DuplicateResourceException if the email or mobile number already exists
     * @throws ResourceNotFoundException if the role code does not exist
     */
    @Transactional
    public User createUser(String email, String password, String mobileNumber, String roleCode) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email address is already registered");
        }
        if (userRepository.existsByMobileNumber(mobileNumber)) {
            throw new DuplicateResourceException("Mobile number is already registered");
        }

        Role role = roleRepository.findByRoleCode(roleCode)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleCode));

        User user = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .mobileNumber(mobileNumber)
                .status("INACTIVE")
                .role(role)
                .failedLoginAttempts(0)
                .forcePasswordChange(false)
                .isDeleted(false)
                .build();

        User savedUser = userRepository.save(user);
        log.info("Successfully created user account: {}", email);
        return savedUser;
    }

    /**
     * Increments the count of failed login attempts for a user. If the count reaches 5,
     * locks the user's account by setting a lockout_until timestamp to 30 minutes in the future.
     *
     * @param user the user who failed a login attempt
     */
    @Transactional
    public void handleFailedLogin(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);

        if (attempts >= 5) {
            user.setLockoutUntil(LocalDateTime.now().plusMinutes(30));
            user.setStatus("LOCKED");
            log.warn("User account locked due to excessive failed login attempts: {}", user.getEmail());
        }

        userRepository.save(user);
    }

    /**
     * Resets the failed login counter and clears the lockout timestamp when a user
     * logs in successfully.
     *
     * @param user the successfully authenticated user
     */
    @Transactional
    public void resetFailedLoginAttempts(User user) {
        if (user.getFailedLoginAttempts() > 0 || user.getLockoutUntil() != null) {
            user.setFailedLoginAttempts(0);
            user.setLockoutUntil(null);
            if ("LOCKED".equals(user.getStatus())) {
                user.setStatus("ACTIVE"); // Unlock if locked only by failed login attempts policy
            }
            userRepository.save(user);
            log.info("Reset failed login parameters for user: {}", user.getEmail());
        }
    }

    /**
     * Records the current timestamp as the user's last successful login time.
     *
     * @param user the successfully authenticated user
     */
    @Transactional
    public void recordSuccessfulLogin(User user) {
        user.setLastSuccessfulLoginAt(LocalDateTime.now());
        resetFailedLoginAttempts(user); // Reset failed counters concurrently
        userRepository.save(user);
    }
}
