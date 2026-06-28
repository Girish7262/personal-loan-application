package com.personalloan.module.document.internal.service;

import com.personalloan.common.exception.BusinessException;
import com.personalloan.common.exception.ResourceNotFoundException;
import com.personalloan.module.document.api.dto.DocumentResponse;
import com.personalloan.module.document.api.dto.DocumentType;
import com.personalloan.module.document.internal.entity.Document;
import com.personalloan.module.document.internal.repository.DocumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final StorageProvider storageProvider;
    private final VirusScanner virusScanner;
    private final FileHashService fileHashService;
    private final DocumentValidationService documentValidationService;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @Autowired
    public DocumentService(
            DocumentRepository documentRepository,
            @Qualifier("localStorageProvider") StorageProvider storageProvider,
            VirusScanner virusScanner,
            FileHashService fileHashService,
            DocumentValidationService documentValidationService) {
        this.documentRepository = documentRepository;
        this.storageProvider = storageProvider;
        this.virusScanner = virusScanner;
        this.fileHashService = fileHashService;
        this.documentValidationService = documentValidationService;
    }

    /**
     * Uploads and stores a document attachment. Validates format (PDF/JPG/PNG),
     * asserts size (<= 5MB), validates magic bytes, checks for content duplicates via SHA-256 hash,
     * runs virus scanning checks, and saves database metadata.
     */
    @Transactional
    public DocumentResponse uploadDocument(Long loanId, DocumentType documentType, String fileName, String contentType, long fileSize, byte[] fileData, String currentUserEmail) {
        log.info("Uploading document type {} for Loan ID: {}, File size: {} bytes", documentType, loanId, fileSize);

        // 1. Validate file format/content type
        if (contentType == null || (!contentType.equals("application/pdf") &&
                                    !contentType.equals("image/jpeg") &&
                                    !contentType.equals("image/png") &&
                                    !contentType.equals("image/jpg"))) {
            throw new BusinessException("Invalid file format. Only PDF, JPG, and PNG formats are allowed.");
        }

        // 2. Validate file size (<= 5MB)
        if (fileSize > MAX_FILE_SIZE) {
            throw new BusinessException("File size exceeds the maximum limit of 5MB.");
        }

        // 3. Validate magic bytes signature integrity
        documentValidationService.validateFileSignature(fileData, contentType);

        // 4. Calculate SHA-256 hash and check duplicate uploads
        String fileHash = fileHashService.calculateSha256(fileData);
        documentRepository.findByFileHashAndIsDeletedFalse(fileHash).ifPresent(existingDoc -> {
            throw new BusinessException("A document with the exact same content has already been uploaded for loan ID: " + existingDoc.getLoanId());
        });

        // 5. Scan for viruses
        virusScanner.scan(fileData, fileName);

        // 6. Save file payload outside the database using the storage provider
        String filePath = storageProvider.store(fileData, fileName);

        // 7. Build and save the metadata entity
        Document document = Document.builder()
                .loanId(loanId)
                .fileName(fileName)
                .filePath(filePath)
                .fileHash(fileHash)
                .fileType(contentType)
                .fileSize(fileSize)
                .documentType(documentType)
                .createdBy(currentUserEmail)
                .isDeleted(false)
                .build();

        Document saved = documentRepository.save(document);
        log.info("Successfully persisted document metadata row ID: {} at path: {}", saved.getDocumentId(), filePath);

        return mapToResponse(saved);
    }

    /**
     * Retrieves the physical bytes of the file for downloads.
     */
    @Transactional(readOnly = true)
    public byte[] downloadDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document attachment details not found"));

        if (document.getIsDeleted()) {
            throw new BusinessException("Requested document has been deleted");
        }

        return storageProvider.retrieve(document.getFilePath());
    }

    /**
     * Retrieves all active document metadata rows associated with a loan application.
     */
    @Transactional(readOnly = true)
    public List<DocumentResponse> getLoanDocuments(Long loanId) {
        return documentRepository.findByLoanIdAndIsDeletedFalse(loanId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Soft-deletes document database metadata and physically wipes files from storage.
     */
    @Transactional
    public void deleteDocument(Long documentId, String currentUserEmail) {
        log.info("Deleting document metadata ID: {}", documentId);
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document details not found"));

        if (document.getIsDeleted()) {
            return;
        }

        // 1. Soft delete database metadata row
        document.setIsDeleted(true);
        document.setUpdatedBy(currentUserEmail);
        document.setUpdatedAt(LocalDateTime.now());
        documentRepository.save(document);

        // 2. Physically purge file from disk
        storageProvider.delete(document.getFilePath());
        log.info("Purged physical file from disk at: {}", document.getFilePath());
    }

    private DocumentResponse mapToResponse(Document doc) {
        String downloadUrl = "/api/v1/documents/" + doc.getDocumentId() + "/download";
        return DocumentResponse.builder()
                .documentId(doc.getDocumentId())
                .loanId(doc.getLoanId())
                .fileName(doc.getFileName())
                .fileSize(doc.getFileSize())
                .fileType(doc.getFileType())
                .documentType(doc.getDocumentType().name())
                .downloadUrl(downloadUrl)
                .createdAt(doc.getCreatedAt())
                .createdBy(doc.getCreatedBy())
                .build();
    }
}
