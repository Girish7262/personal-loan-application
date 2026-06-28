package com.personalloan.module.document.internal.service;

import com.personalloan.common.exception.BusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileHashServiceTest {

    private final FileHashService fileHashService = new FileHashService();

    @Test
    void calculateSha256_WithValidBytes_ShouldReturnCorrectHexHash() {
        // Arrange
        byte[] data = "Hello Personal Loan Application".getBytes();
        
        // Calculated SHA-256 for "Hello Personal Loan Application" is:
        // 9439c0993ad4ee3a139a053cbf2b8006a88b1cc9bdeaf290562e84860b001a1c
        String expectedHash = "9439c0993ad4ee3a139a053cbf2b8006a88b1cc9bdeaf290562e84860b001a1c";

        // Act
        String calculated = fileHashService.calculateSha256(data);

        // Assert
        assertEquals(expectedHash, calculated);
    }

    @Test
    void calculateSha256_WithNullBytes_ShouldThrowBusinessException() {
        assertThrows(BusinessException.class, () -> fileHashService.calculateSha256(null));
    }
}
