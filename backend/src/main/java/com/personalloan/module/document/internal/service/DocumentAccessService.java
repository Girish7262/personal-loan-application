package com.personalloan.module.document.internal.service;

import com.personalloan.common.exception.BusinessException;
import com.personalloan.module.document.internal.entity.Document;
import com.personalloan.module.loan.api.LoanFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentAccessService {

    private final LoanFacade loanFacade;

    /**
     * Authorizes access to the requested document.
     * Delegates validation to LoanFacade to confirm the logged-in customer owns the associated loan application.
     *
     * @param document the document metadata entity
     * @param userId the active logged-in user ID
     */
    public void verifyAccess(Document document, Long userId) {
        log.info("Verifying user access permission for document ID: {}, User ID: {}", document.getDocumentId(), userId);

        if (document.getLoanId() == null) {
            throw new BusinessException("Document is not associated with any active loan application");
        }

        try {
            // LoanFacade.getApplication validates that the user owns the loan application.
            // If the user does not own it, it automatically throws a BusinessException/AccessDenied.
            loanFacade.getApplication(document.getLoanId(), userId);
        } catch (Exception e) {
            log.error("Access validation failed for user ID: {} attempting to read document ID: {}", userId, document.getDocumentId(), e);
            throw new BusinessException("Access denied. You do not have permissions to read this document.");
        }

        log.info("Access authorized for document ID: {}", document.getDocumentId());
    }
}
