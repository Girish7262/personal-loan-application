package com.personalloan.module.customer;

import com.personalloan.BaseIntegrationTest;
import com.personalloan.module.customer.api.dto.ProfileStatus;
import com.personalloan.module.customer.internal.entity.CustomerProfile;
import com.personalloan.module.customer.internal.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CustomerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void cleanDatabase() {
        customerRepository.deleteAll();
    }

    @Test
    void saveProfile_ShouldStoreEncryptedAadhaarInDatabase() {
        // Arrange
        CustomerProfile profile = CustomerProfile.builder()
                .userId(301L)
                .firstName("Vijay")
                .lastName("Kumar")
                .dateOfBirth(LocalDate.of(1990, 8, 15))
                .gender("MALE")
                .fatherName("Rajesh Kumar")
                .mobileNumber("9999988888")
                .email("vijay@gmail.com")
                .panNumber("ABCDE9999F")
                .aadhaarNumber("GCM_ENCRYPTED_PAYLOAD_BASE64_VALUE")
                .street("High Street")
                .city("Mumbai")
                .state("Maharashtra")
                .pincode("400001")
                .employmentType("SALARIED")
                .monthlyIncome(BigDecimal.valueOf(75000))
                .profileStatus(ProfileStatus.COMPLETE)
                .isDeleted(false)
                .build();

        // Act
        CustomerProfile saved = customerRepository.save(profile);

        // Assert
        assertNotNull(saved.getCustomerId());
        Optional<CustomerProfile> fetched = customerRepository.findById(saved.getCustomerId());
        assertTrue(fetched.isPresent());
        // Verify that it is stored encrypted (equals to GCM_ENCRYPTED_PAYLOAD_BASE64_VALUE, not raw Aadhaar!)
        assertEquals("GCM_ENCRYPTED_PAYLOAD_BASE64_VALUE", fetched.get().getAadhaarNumber());
        assertEquals(0, fetched.get().getVersion()); // Initial version should be 0
    }

    @Test
    void saveDuplicatePan_ShouldThrowDataIntegrityViolationException() {
        // Arrange
        CustomerProfile profile1 = CustomerProfile.builder()
                .userId(401L)
                .firstName("Amit")
                .lastName("Sharma")
                .dateOfBirth(LocalDate.of(1992, 1, 1))
                .gender("MALE")
                .fatherName("Father")
                .mobileNumber("9876543210")
                .email("amit@gmail.com")
                .panNumber("ABCDE7777G") // Same PAN
                .aadhaarNumber("AADHAAR_HASH_1")
                .street("Street 1")
                .city("Pune")
                .state("State")
                .pincode("411001")
                .employmentType("SALARIED")
                .monthlyIncome(BigDecimal.valueOf(40000))
                .profileStatus(ProfileStatus.COMPLETE)
                .isDeleted(false)
                .build();

        CustomerProfile profile2 = CustomerProfile.builder()
                .userId(402L)
                .firstName("Sumit")
                .lastName("Sharma")
                .dateOfBirth(LocalDate.of(1993, 1, 1))
                .gender("MALE")
                .fatherName("Father")
                .mobileNumber("9876543211")
                .email("sumit@gmail.com")
                .panNumber("ABCDE7777G") // Same PAN
                .aadhaarNumber("AADHAAR_HASH_2")
                .street("Street 2")
                .city("Pune")
                .state("State")
                .pincode("411001")
                .employmentType("SALARIED")
                .monthlyIncome(BigDecimal.valueOf(45000))
                .profileStatus(ProfileStatus.COMPLETE)
                .isDeleted(false)
                .build();

        customerRepository.save(profile1);

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> customerRepository.saveAndFlush(profile2));
    }

    @Test
    void optimisticLocking_ShouldThrowConcurrencyFailure() {
        // Arrange
        CustomerProfile profile = CustomerProfile.builder()
                .userId(501L)
                .firstName("Rohan")
                .lastName("Joshi")
                .dateOfBirth(LocalDate.of(1994, 2, 2))
                .gender("MALE")
                .fatherName("Father")
                .mobileNumber("9876543212")
                .email("rohan@gmail.com")
                .panNumber("ABCDE8888H")
                .aadhaarNumber("AADHAAR_HASH_3")
                .street("Street")
                .city("Pune")
                .state("State")
                .pincode("411001")
                .employmentType("SALARIED")
                .monthlyIncome(BigDecimal.valueOf(50000))
                .profileStatus(ProfileStatus.COMPLETE)
                .isDeleted(false)
                .build();

        CustomerProfile saved = customerRepository.saveAndFlush(profile);
        assertEquals(0, saved.getVersion());

        // Load two separate instances of the same database row
        CustomerProfile instance1 = customerRepository.findById(saved.getCustomerId()).orElseThrow();
        CustomerProfile instance2 = customerRepository.findById(saved.getCustomerId()).orElseThrow();

        // Act - Modify and save instance1 (increments version in DB to 1)
        instance1.setFirstName("Rohan Edited");
        customerRepository.saveAndFlush(instance1);

        // Act - Modify instance2 (still holds version = 0) and try to save
        instance2.setFirstName("Rohan Edited Conflict");

        // Assert - Saving instance2 must fail because version in DB is now 1, but instance2 has version 0
        assertThrows(ObjectOptimisticLockingFailureException.class, () -> customerRepository.saveAndFlush(instance2));
    }
}
