package com.personalloan.module.loan.api;

import com.personalloan.module.loan.api.dto.LoanApplicationRequest;
import com.personalloan.module.loan.api.dto.LoanApplicationResponse;
import com.personalloan.module.loan.api.dto.LoanStatus;

import java.util.List;

public interface LoanFacade {

    /**
     * Submits a new loan application. Calculates EMI, validates eligibility, and saves to database.
     *
     * @param userId the user ID applying
     * @param request the application request DTO
     * @param currentUserEmail the active user email
     * @return the saved application details DTO
     */
    LoanApplicationResponse submitApplication(Long userId, LoanApplicationRequest request, String currentUserEmail);

    /**
     * Retrieves all loan applications submitted by the customer associated with the user ID.
     *
     * @param userId the user ID
     * @return list of loan applications
     */
    List<LoanApplicationResponse> getApplications(Long userId);

    /**
     * Retrieves specific application details. Asserts owner security bounds.
     *
     * @param loanId the loan application database ID
     * @param userId the user ID attempting the retrieve
     * @return the application details DTO
     */
    LoanApplicationResponse getApplication(Long loanId, Long userId);

    /**
     * Updates/transitions application status (e.g. APPROVED, REJECTED, SANCTIONED, DISBURSED).
     *
     * @param loanId the loan application database ID
     * @param targetStatus the desired workflow status
     * @param currentUserEmail the active actor email
     * @return the updated application details DTO
     */
    LoanApplicationResponse updateApplicationStatus(Long loanId, LoanStatus targetStatus, String currentUserEmail);
}
