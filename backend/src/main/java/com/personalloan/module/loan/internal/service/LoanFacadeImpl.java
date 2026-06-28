package com.personalloan.module.loan.internal.service;

import com.personalloan.module.loan.api.LoanFacade;
import com.personalloan.module.loan.api.dto.LoanApplicationRequest;
import com.personalloan.module.loan.api.dto.LoanApplicationResponse;
import com.personalloan.module.loan.api.dto.LoanStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanFacadeImpl implements LoanFacade {

    private final LoanService loanService;

    @Override
    @Transactional
    public LoanApplicationResponse submitApplication(Long userId, LoanApplicationRequest request, String currentUserEmail) {
        return loanService.submitApplication(userId, request, currentUserEmail);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanApplicationResponse> getApplications(Long userId) {
        return loanService.getApplications(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public LoanApplicationResponse getApplication(Long loanId, Long userId) {
        return loanService.getApplication(loanId, userId);
    }

    @Override
    @Transactional
    public LoanApplicationResponse updateApplicationStatus(Long loanId, LoanStatus targetStatus, String currentUserEmail) {
        return loanService.updateApplicationStatus(loanId, targetStatus, currentUserEmail);
    }
}
