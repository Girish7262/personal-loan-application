package com.personalloan.module.document.api;

import com.personalloan.module.document.api.dto.DocumentResponse;
import com.personalloan.module.document.api.dto.DocumentType;

import java.util.List;

public interface DocumentFacade {

    /**
     * Uploads and stores a new document.
     */
    DocumentResponse uploadDocument(Long loanId, DocumentType documentType, String fileName, String contentType, long fileSize, byte[] fileData, String currentUserEmail);

    /**
     * Retrieves the file payload data bytes.
     */
    byte[] downloadDocument(Long documentId, Long userId);

    /**
     * Lists all uploaded documents metadata for a loan application.
     */
    List<DocumentResponse> getLoanDocuments(Long loanId);

    /**
     * Deletes the document metadata and physical file content.
     */
    void deleteDocument(Long documentId, String currentUserEmail);
}
