package com.personalloan.module.document.internal.service;

import com.personalloan.common.exception.ResourceNotFoundException;
import com.personalloan.module.document.internal.entity.Document;
import com.personalloan.module.document.internal.repository.DocumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class DocumentDownloadService {

    private final DocumentRepository documentRepository;
    private final StorageProvider storageProvider;
    private final DocumentAccessService documentAccessService;

    @Autowired
    public DocumentDownloadService(
            DocumentRepository documentRepository,
            @Qualifier("localStorageProvider") StorageProvider storageProvider,
            DocumentAccessService documentAccessService) {
        this.documentRepository = documentRepository;
        this.storageProvider = storageProvider;
        this.documentAccessService = documentAccessService;
    }

    /**
     * Secures and retrieves physical file byte arrays for downloads.
     *
     * @param documentId the database metadata ID
     * @param userId the active user ID requesting download
     * @return raw file bytes
     */
    @Transactional(readOnly = true)
    public byte[] downloadDocument(Long documentId, Long userId) {
        log.info("Secure download requested for document ID: {}, User ID: {}", documentId, userId);

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document metadata attachment details not found"));

        if (document.getIsDeleted()) {
            throw new ResourceNotFoundException("Requested document has been deleted");
        }

        // Validate access authorization
        documentAccessService.verifyAccess(document, userId);

        // Fetch physical file from storage provider
        return storageProvider.retrieve(document.getFilePath());
    }
}
