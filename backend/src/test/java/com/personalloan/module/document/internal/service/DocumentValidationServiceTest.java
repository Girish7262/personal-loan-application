package com.personalloan.module.document.internal.service;

import com.personalloan.common.exception.BusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DocumentValidationServiceTest {

    private final DocumentValidationService validationService = new DocumentValidationService();

    @Test
    void validateFileSignature_WithValidPdfMagicBytes_ShouldNotThrowException() {
        // Arrange: Magic bytes for PDF are 0x25, 0x50, 0x44, 0x46 (%PDF)
        byte[] pdfBytes = new byte[]{0x25, 0x50, 0x44, 0x46, 0x11, 0x22};

        // Act & Assert
        assertDoesNotThrow(() -> validationService.validateFileSignature(pdfBytes, "application/pdf"));
    }

    @Test
    void validateFileSignature_WithInvalidPdfBytes_ShouldThrowBusinessException() {
        // Arrange
        byte[] badBytes = new byte[]{0x11, 0x22, 0x33, 0x44};

        // Act & Assert
        assertThrows(BusinessException.class, () -> 
                validationService.validateFileSignature(badBytes, "application/pdf"));
    }

    @Test
    void validateFileSignature_WithValidPngBytes_ShouldNotThrowException() {
        // Arrange: PNG magic: 89 50 4E 47
        byte[] pngBytes = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47};

        // Act & Assert
        assertDoesNotThrow(() -> validationService.validateFileSignature(pngBytes, "image/png"));
    }

    @Test
    void validateFileSignature_WithValidJpegBytes_ShouldNotThrowException() {
        // Arrange: JPEG magic: FF D8 FF
        byte[] jpegBytes = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 0x11};

        // Act & Assert
        assertDoesNotThrow(() -> validationService.validateFileSignature(jpegBytes, "image/jpeg"));
        assertDoesNotThrow(() -> validationService.validateFileSignature(jpegBytes, "image/jpg"));
    }

    @Test
    void validateFileSignature_WithUnsupportedMime_ShouldThrowBusinessException() {
        byte[] data = new byte[]{0x11, 0x22, 0x33, 0x44};
        assertThrows(BusinessException.class, () -> 
                validationService.validateFileSignature(data, "text/plain"));
    }
}
