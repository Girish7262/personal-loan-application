package com.personalloan.module.customer.internal.service;

import com.personalloan.common.exception.BusinessException;
import com.personalloan.common.exception.DuplicateResourceException;
import com.personalloan.module.customer.api.dto.*;
import com.personalloan.module.customer.internal.entity.CustomerProfile;
import com.personalloan.module.customer.internal.mapper.CustomerMapper;
import com.personalloan.module.customer.internal.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ValidationService validationService;
    @Mock
    private EncryptionService encryptionService;
    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    private Long userId;
    private CustomerProfileRequest request;
    private CustomerProfile profile;

    @BeforeEach
    void setUp() {
        userId = 100L;
        request = CustomerProfileRequest.builder()
                .firstName("Girish")
                .lastName("Patil")
                .dateOfBirth(LocalDate.of(1995, 5, 10))
                .gender("MALE")
                .fatherName("Ramesh Patil")
                .mobileNumber("9876543210")
                .email("girish@gmail.com")
                .panNumber("ABCDE1234F")
                .aadhaarNumber("123412341234")
                .street("Main Street")
                .city("Pune")
                .state("Maharashtra")
                .pincode("411001")
                .employmentType("SALARIED")
                .companyName("Tech Corp")
                .monthlyIncome(BigDecimal.valueOf(50000))
                .build();

        profile = CustomerProfile.builder()
                .customerId(1L)
                .userId(userId)
                .firstName("Girish")
                .lastName("Patil")
                .dateOfBirth(LocalDate.of(1995, 5, 10))
                .gender("MALE")
                .fatherName("Ramesh Patil")
                .mobileNumber("9876543210")
                .email("girish@gmail.com")
                .panNumber("ABCDE1234F")
                .aadhaarNumber("ENCRYPTED_AADHAAR")
                .street("Main Street")
                .city("Pune")
                .state("Maharashtra")
                .pincode("411001")
                .employmentType("SALARIED")
                .companyName("Tech Corp")
                .monthlyIncome(BigDecimal.valueOf(50000))
                .profileStatus(ProfileStatus.COMPLETE)
                .build();
    }

    @Test
    void getProfile_WhenExists_ShouldReturnResponseWithMaskedDetails() {
        // Arrange
        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(encryptionService.decrypt("ENCRYPTED_AADHAAR")).thenReturn("123412341234");
        when(encryptionService.maskAadhaar("123412341234")).thenReturn("XXXXXXXX1234");
        when(encryptionService.maskPan("ABCDE1234F")).thenReturn("XXXXXX1234");

        // Act
        CustomerProfileResponse response = customerService.getProfile(userId);

        // Assert
        assertNotNull(response);
        assertEquals(userId, response.getUserId());
        assertEquals("XXXXXX1234", response.getPanNumber());
        assertEquals("XXXXXXXX1234", response.getAadhaarNumber());
        assertEquals(ProfileStatus.COMPLETE.name(), response.getProfileStatus());
        verify(customerRepository, times(1)).findByUserId(userId);
    }

    @Test
    void getProfile_WhenNotExists_ShouldReturnIncompleteShell() {
        // Arrange
        when(customerRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // Act
        CustomerProfileResponse response = customerService.getProfile(userId);

        // Assert
        assertNotNull(response);
        assertEquals(userId, response.getUserId());
        assertEquals(ProfileStatus.INCOMPLETE.name(), response.getProfileStatus());
        assertNull(response.getFirstName());
    }

    @Test
    void createProfile_WhenProfileAlreadyExists_ShouldThrowDuplicateResourceException() {
        // Arrange
        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(profile));

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> 
                customerService.createProfile(userId, request, "test@gmail.com"));
    }

    @Test
    void createProfile_WhenValidRequest_ShouldEncryptAadhaarAndSave() {
        // Arrange
        when(customerRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(encryptionService.encrypt("123412341234")).thenReturn("ENCRYPTED_AADHAAR");
        when(customerRepository.save(any(CustomerProfile.class))).thenReturn(profile);
        when(encryptionService.maskAadhaar("123412341234")).thenReturn("XXXXXXXX1234");
        when(encryptionService.maskPan("ABCDE1234F")).thenReturn("XXXXXX1234");

        // Act
        CustomerProfileResponse response = customerService.createProfile(userId, request, "test@gmail.com");

        // Assert
        assertNotNull(response);
        assertEquals("XXXXXX1234", response.getPanNumber());
        assertEquals("XXXXXXXX1234", response.getAadhaarNumber());
        verify(validationService, times(1)).validateAge(request.getDateOfBirth());
        verify(validationService, times(1)).validatePan(request.getPanNumber());
        verify(customerRepository, times(1)).save(any(CustomerProfile.class));
    }

    @Test
    void patchProfile_WhenOmittedFields_ShouldOnlyValidateProvidedFields() {
        // Arrange
        CustomerProfileRequest patchRequest = CustomerProfileRequest.builder()
                .city("Mumbai")
                .monthlyIncome(BigDecimal.valueOf(60000))
                .build();

        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(customerRepository.save(profile)).thenReturn(profile);
        when(encryptionService.decrypt("ENCRYPTED_AADHAAR")).thenReturn("123412341234");
        when(encryptionService.maskAadhaar("123412341234")).thenReturn("XXXXXXXX1234");
        when(encryptionService.maskPan("ABCDE1234F")).thenReturn("XXXXXX1234");

        // Act
        CustomerProfileResponse response = customerService.patchProfile(userId, patchRequest, "test@gmail.com");

        // Assert
        assertNotNull(response);
        verify(validationService, never()).validateAge(any());
        verify(validationService, never()).validatePan(any());
        verify(customerMapper, times(1)).updateEntityFromRequest(patchRequest, profile);
        verify(customerRepository, times(1)).save(profile);
    }

    @Test
    void getProfileCompletion_WhenProfileIsMissing_ShouldReturnZeroPercentage() {
        // Arrange
        when(customerRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // Act
        ProfileCompletionResponse response = customerService.getProfileCompletion(userId);

        // Assert
        assertNotNull(response);
        assertEquals(0, response.percentage());
        assertEquals(ProfileStatus.INCOMPLETE, response.status());
        assertTrue(response.missingFields().contains("firstName"));
        assertTrue(response.missingFields().contains("monthlyIncome"));
    }

    @Test
    void getProfileCompletion_WhenAllFieldsArePresent_ShouldReturn100PercentageAndComplete() {
        // Arrange
        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(profile));

        // Act
        ProfileCompletionResponse response = customerService.getProfileCompletion(userId);

        // Assert
        assertNotNull(response);
        assertEquals(100, response.percentage());
        assertEquals(ProfileStatus.COMPLETE, response.status());
        assertTrue(response.missingFields().isEmpty());
    }
}
