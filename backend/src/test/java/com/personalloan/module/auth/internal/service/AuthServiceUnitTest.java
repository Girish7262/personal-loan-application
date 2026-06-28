package com.personalloan.module.auth.internal.service;

import com.personalloan.common.exception.BusinessException;
import com.personalloan.common.exception.UnauthorizedException;
import com.personalloan.module.auth.api.dto.ChangePasswordRequest;
import com.personalloan.module.auth.api.dto.RegisterRequest;
import com.personalloan.module.auth.internal.entity.Role;
import com.personalloan.module.auth.internal.entity.User;
import com.personalloan.module.auth.internal.repository.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceUnitTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @Mock
    private EmailVerificationService emailVerificationService;
    @Mock
    private PasswordPolicyService passwordPolicyService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private PasswordHistoryService passwordHistoryService;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private Role customerRole;

    @BeforeEach
    void setUp() {
        customerRole = Role.builder().roleId(1L).roleCode("CUSTOMER").build();
        testUser = User.builder()
                .userId(1L)
                .email("test@example.com")
                .passwordHash("hashed_password")
                .status("ACTIVE")
                .role(customerRole)
                .forcePasswordChange(false)
                .build();
    }

    @Test
    void register_ShouldCallDependencyServicesOnValidRequest() {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("test@example.com")
                .mobileNumber("9876543210")
                .password("Password@123")
                .confirmPassword("Password@123")
                .build();

        when(userService.createUser(any(), any(), any(), any())).thenReturn(testUser);

        // Act
        authService.register(request);

        // Assert
        verify(passwordPolicyService).validatePassword(request.getPassword(), request.getConfirmPassword());
        verify(userService).createUser(request.getEmail(), request.getPassword(), request.getMobileNumber(), "CUSTOMER");
        verify(emailVerificationService).sendVerificationEmail(testUser);
    }

    @Test
    void changePassword_ShouldThrowUnauthorizedException_WhenOldPasswordDoesNotMatch() {
        // Arrange
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword("wrong_old_password")
                .newPassword("NewPassword@123")
                .confirmPassword("NewPassword@123")
                .build();

        when(passwordEncoder.matches(request.getOldPassword(), testUser.getPasswordHash())).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> authService.changePassword(testUser, request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void changePassword_ShouldSaveNewHashedPassword_WhenRequestIsValid() {
        // Arrange
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword("correct_old_password")
                .newPassword("NewPassword@123")
                .confirmPassword("NewPassword@123")
                .build();

        when(passwordEncoder.matches(request.getOldPassword(), testUser.getPasswordHash())).thenReturn(true);
        when(passwordEncoder.encode(request.getNewPassword())).thenReturn("new_hashed_password");

        // Act
        authService.changePassword(testUser, request);

        // Assert
        verify(passwordPolicyService).validatePassword(request.getNewPassword(), request.getConfirmPassword());
        verify(passwordHistoryService).validatePasswordNotUsed(testUser, request.getNewPassword());
        verify(passwordHistoryService).recordPasswordChange(testUser, "hashed_password");
        assertEquals("new_hashed_password", testUser.getPasswordHash());
        verify(userRepository).save(testUser);
    }
}
