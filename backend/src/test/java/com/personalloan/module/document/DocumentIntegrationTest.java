package com.personalloan.module.document;

import com.personalloan.BaseIntegrationTest;
import com.personalloan.module.document.api.dto.DocumentType;
import com.personalloan.module.document.internal.entity.Document;
import com.personalloan.module.document.internal.repository.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DocumentIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private DocumentRepository documentRepository;

    @BeforeEach
    void cleanDatabase() {
        documentRepository.deleteAll();
    }

    @Test
    void saveAndQueryDocument_ShouldPersistAndRetrieveMetadata() {
        // Arrange
        Document doc = Document.builder()
                .loanId(1L)
                .fileName("test_kyc.pdf")
                .filePath("/uploads/unique_test_kyc.pdf")
                .fileHash("hash1234567890abcdef1234567890abcdef")
                .fileType("application/pdf")
                .fileSize(100L)
                .documentType(DocumentType.AADHAAR)
                .createdBy("girish@gmail.com")
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        // Act
        Document saved = documentRepository.saveAndFlush(doc);

        // Assert
        assertNotNull(saved.getDocumentId());
        
        // Query by Loan ID
        List<Document> activeDocs = documentRepository.findByLoanIdAndIsDeletedFalse(1L);
        assertEquals(1, activeDocs.size());
        assertEquals("test_kyc.pdf", activeDocs.get(0).getFileName());

        // Query by Hash
        Optional<Document> hashedDoc = documentRepository.findByFileHashAndIsDeletedFalse("hash1234567890abcdef1234567890abcdef");
        assertTrue(hashedDoc.isPresent());
        assertEquals(saved.getDocumentId(), hashedDoc.get().getDocumentId());
    }
}
