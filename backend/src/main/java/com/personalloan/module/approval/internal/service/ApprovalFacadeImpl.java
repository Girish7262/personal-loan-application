package com.personalloan.module.approval.internal.service;

import com.personalloan.module.approval.api.ApprovalFacade;
import com.personalloan.module.approval.api.dto.ApprovalHistoryResponse;
import com.personalloan.module.approval.internal.entity.ApprovalHistory;
import com.personalloan.module.approval.internal.repository.ApprovalHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApprovalFacadeImpl implements ApprovalFacade {

    private final ApprovalHistoryRepository approvalHistoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ApprovalHistoryResponse> getApprovalHistory(Long loanId) {
        return approvalHistoryRepository.findByLoanIdOrderByActionDateDesc(loanId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void logApprovalHistory(
            Long loanId,
            String action,
            Long actorId,
            String remarks,
            BigDecimal recommendedAmount,
            BigDecimal approvedAmount,
            BigDecimal interestRate) {

        ApprovalHistory logEntry = ApprovalHistory.builder()
                .loanId(loanId)
                .action(action)
                .actorId(actorId)
                .remarks(remarks)
                .recommendedAmount(recommendedAmount)
                .approvedAmount(approvedAmount)
                .interestRate(interestRate)
                .build();

        approvalHistoryRepository.save(logEntry);
    }

    private ApprovalHistoryResponse mapToResponse(ApprovalHistory entity) {
        return ApprovalHistoryResponse.builder()
                .approvalId(entity.getApprovalId())
                .loanId(entity.getLoanId())
                .action(entity.getAction())
                .actorId(entity.getActorId())
                .remarks(entity.getRemarks())
                .recommendedAmount(entity.getRecommendedAmount())
                .approvedAmount(entity.getApprovedAmount())
                .interestRate(entity.getInterestRate())
                .actionDate(entity.getActionDate())
                .build();
    }
}
