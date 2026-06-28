package com.personalloan.module.document.internal.service;

import com.personalloan.module.document.api.DocumentFacade;
import com.personalloan.module.document.api.dto.DocumentResponse;
import com.personalloan.module.document.api.dto.DocumentType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentFacadeImpl implements DocumentFacade {

    private final DocumentService documentService;
    private final DocumentDownloadService documentDownloadService;

    @Override
    @Transactional
    public DocumentResponse uploadDocument(Long loanId, DocumentType documentType, String fileName, String contentType, long fileSize, byte[] fileData, String currentUserEmail) {
        return documentService.uploadDocument(loanId, documentType, fileName, contentType, fileSize, fileData, currentUserEmail);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] downloadDocument(Long documentId, Long userId) {
        return documentDownloadService.downloadDocument(documentId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponse> getLoanDocuments(Long loanId) {
        return documentService.getLoanDocuments(loanId);
    }

    @Override
    @Transactional
    public void deleteDocument(Long documentId, String currentUserEmail) {
        documentService.deleteDocument(documentId, currentUserEmail);
    }
}
