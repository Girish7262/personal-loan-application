package com.personalloan.module.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personalloan.BaseIntegrationTest;
import com.personalloan.module.auth.dto.LoginRequest;
import com.personalloan.module.auth.entity.Role;
import com.personalloan.module.auth.entity.User;
import com.personalloan.module.auth.repository.RoleRepository;
import com.personalloan.module.auth.repository.UserRepository;
import com.personalloan.common.util.HashUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class AuthFlowIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        Role role = roleRepository.findByRoleCode("CUSTOMER")
                .orElseGet(() -> roleRepository.save(Role.builder().roleCode("CUSTOMER").description("Customer").build()));

        testUser = User.builder()
                .email("integration-" + UUID.randomUUID() + "@example.com")
                .passwordHash(passwordEncoder.encode("Password@123"))
                .mobileNumber("9" + String.format("%09d", (int) (Math.random() * 1000000000L)))
                .status("ACTIVE")
                .role(role)
                .failedLoginAttempts(0)
                .forcePasswordChange(false)
                .isDeleted(false)
                .build();
        userRepository.save(testUser);
    }

    @Test
    void login_ShouldSucceedAndReturnTokens_OnValidCredentials() throws Exception {
        LoginRequest request = new LoginRequest(testUser.getEmail(), "Password@123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andExpect(header().exists("X-Request-ID"));
    }

    @Test
    void login_ShouldLockAccount_After5FailedAttempts() throws Exception {
        LoginRequest request = new LoginRequest(testUser.getEmail(), "WrongPassword");

        // Fail 4 times
        for (int i = 0; i < 4; i++) {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value(containsString("Invalid email or password")));
        }

        // 5th failure triggers lockout
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        // 6th call returns locked message
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(containsString("locked due to excessive failed attempts")));
    }

    @Test
    void emailVerification_ShouldFail_IfLinkHasExpired() throws Exception {
        // Arrange
        String rawToken = "my_raw_secure_test_verification_token";
        String hashedToken = HashUtils.sha256Hex(rawToken);

        testUser.setStatus("INACTIVE");
        testUser.setEmailVerificationToken(hashedToken);
        testUser.setEmailVerificationExpiry(LocalDateTime.now().minusHours(1)); // Expired 1 hour ago
        userRepository.save(testUser);

        // Act & Assert
        mockMvc.perform(get("/api/auth/verify-email")
                        .param("token", rawToken))
                .andExpect(status().isUnprocessableEntity()) // BusinessException -> 422
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("expired")));
    }

    @Test
    void rateLimiter_ShouldReturn429_WhenLimitExceeded() throws Exception {
        // Unique random key for this rate limit test to prevent local test pollution
        String uniqueEmail = "rate-limit-" + UUID.randomUUID() + "@example.com";
        LoginRequest request = new LoginRequest(uniqueEmail, "Password");

        // Perform 5 logins (limit threshold)
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));
        }

        // 6th call should be blocked with 429
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.status").value(429))
                .andExpect(jsonPath("$.error").value("Too Many Requests"));
    }
}
