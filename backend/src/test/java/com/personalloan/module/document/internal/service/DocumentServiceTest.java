package com.personalloan.module.document.internal.service;

import com.personalloan.common.exception.BusinessException;
import com.personalloan.module.document.api.dto.DocumentResponse;
import com.personalloan.module.document.api.dto.DocumentType;
import com.personalloan.module.document.internal.entity.Document;
import com.personalloan.module.document.internal.repository.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock private DocumentRepository documentRepository;
    @Mock private StorageProvider storageProvider;
    @Mock private VirusScanner virusScanner;
    @Mock private FileHashService fileHashService;
    @Mock private DocumentValidationService documentValidationService;

    private DocumentService documentService;

    private Long loanId;
    private byte[] validPdfBytes;
    private String hashValue;

    @BeforeEach
    void setUp() {
        documentService = new DocumentService(
                documentRepository,
                storageProvider,
                virusScanner,
                fileHashService,
                documentValidationService
        );

        loanId = 1L;
        validPdfBytes = new byte[]{0x25, 0x50, 0x44, 0x46, 0x11, 0x22}; // Mock %PDF
        hashValue = "9439c0993ad4ee3a139a053cbf2b8006a88b1cc9bdeaf290562e84860b001a1c";
    }

    @Test
    void uploadDocument_WithValidInputs_ShouldCalculateHashValidateAndPersist() {
        // Arrange
        when(fileHashService.calculateSha256(validPdfBytes)).thenReturn(hashValue);
        when(documentRepository.findByFileHashAndIsDeletedFalse(hashValue)).thenReturn(Optional.empty());
        when(storageProvider.store(validPdfBytes, "kyc.pdf")).thenReturn("/uploads/unique_kyc.pdf");

        Document mockSaved = Document.builder()
                .documentId(10L)
                .loanId(loanId)
                .fileName("kyc.pdf")
                .filePath("/uploads/unique_kyc.pdf")
                .fileHash(hashValue)
                .fileType("application/pdf")
                .fileSize((long) validPdfBytes.length)
                .documentType(DocumentType.AADHAAR)
                .createdBy("girish@gmail.com")
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();
        when(documentRepository.save(any(Document.class))).thenReturn(mockSaved);

        // Act
        DocumentResponse response = documentService.uploadDocument(
                loanId,
                DocumentType.AADHAAR,
                "kyc.pdf",
                "application/pdf",
                validPdfBytes.length,
                validPdfBytes,
                "girish@gmail.com"
        );

        // Assert
        assertNotNull(response);
        assertEquals(10L, response.getDocumentId());
        assertEquals("kyc.pdf", response.getFileName());
        verify(documentValidationService, times(1)).validateFileSignature(validPdfBytes, "application/pdf");
        verify(virusScanner, times(1)).scan(validPdfBytes, "kyc.pdf");
        verify(storageProvider, times(1)).store(validPdfBytes, "kyc.pdf");
        verify(documentRepository, times(1)).save(any(Document.class));
    }

    @Test
    void uploadDocument_WithDuplicateHash_ShouldThrowBusinessException() {
        // Arrange
        when(fileHashService.calculateSha256(validPdfBytes)).thenReturn(hashValue);
        Document existing = Document.builder().loanId(2L).build();
        when(documentRepository.findByFileHashAndIsDeletedFalse(hashValue)).thenReturn(Optional.of(existing));

        // Act & Assert
        assertThrows(BusinessException.class, () ->
                documentService.uploadDocument(
                        loanId,
                        DocumentType.AADHAAR,
                        "kyc.pdf",
                        "application/pdf",
                        validPdfBytes.length,
                        validPdfBytes,
                        "girish@gmail.com"
                ));

        verify(storageProvider, never()).store(any(), any());
        verify(documentRepository, never()).save(any());
    }

    @Test
    void uploadDocument_WithUnsupportedMimeType_ShouldThrowBusinessException() {
        assertThrows(BusinessException.class, () ->
                documentService.uploadDocument(
                        loanId,
                        DocumentType.PAN,
                        "notes.txt",
                        "text/plain",
                        100,
                        new byte[]{1, 2, 3},
                        "girish@gmail.com"
                ));
    }

    @Test
    void uploadDocument_WithExceededSize_ShouldThrowBusinessException() {
        long badSize = 6 * 1024 * 1024; // 6MB
        assertThrows(BusinessException.class, () ->
                documentService.uploadDocument(
                        loanId,
                        DocumentType.PAN,
                        "large.pdf",
                        "application/pdf",
                        badSize,
                        validPdfBytes,
                        "girish@gmail.com"
                ));
    }

    @Test
    void deleteDocument_ShouldSoftDeleteMetadataAndPhysicallyPurgeFile() {
        // Arrange
        Document existing = Document.builder()
                .documentId(10L)
                .filePath("/uploads/file.pdf")
                .isDeleted(false)
                .build();
        when(documentRepository.findById(10L)).thenReturn(Optional.of(existing));

        // Act
        documentService.deleteDocument(10L, "girish@gmail.com");

        // Assert
        assertTrue(existing.getIsDeleted());
        assertEquals("girish@gmail.com", existing.getUpdatedBy());
        verify(documentRepository, times(1)).save(existing);
        verify(storageProvider, times(1)).delete("/uploads/file.pdf");
    }
}
