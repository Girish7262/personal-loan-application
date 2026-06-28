package com.personalloan.module.document.internal.repository;

import com.personalloan.module.document.api.dto.DocumentType;
import com.personalloan.module.document.internal.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    /**
     * Finds active documents associated with a specific loan application ID.
     */
    List<Document> findByLoanIdAndIsDeletedFalse(Long loanId);

    /**
     * Finds a specific document type for a loan application that is not deleted.
     */
    Optional<Document> findByLoanIdAndDocumentTypeAndIsDeletedFalse(Long loanId, DocumentType documentType);

    /**
     * Finds an active document by its file SHA-256 hash to prevent duplicate uploads.
     */
    Optional<Document> findByFileHashAndIsDeletedFalse(String fileHash);
}
