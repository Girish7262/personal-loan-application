package com.personalloan.module.loan.internal.service;

import com.personalloan.module.loan.event.LoanStatusChangedEvent;
import com.personalloan.module.loan.internal.entity.LoanStatusHistory;
import com.personalloan.module.loan.internal.repository.LoanStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoanStatusHistoryListener {

    private final LoanStatusHistoryRepository statusHistoryRepository;

    /**
     * Intercepts LoanStatusChangedEvent and writes a history entry row.
     */
    @EventListener
    @Transactional
    public void onLoanStatusChanged(LoanStatusChangedEvent event) {
        log.info("Recording status transition history log for Loan ID: {} from {} to {}",
                event.getLoanId(), event.getOldStatus(), event.getNewStatus());

        LoanStatusHistory history = LoanStatusHistory.builder()
                .loanId(event.getLoanId())
                .fromStatus(event.getOldStatus())
                .toStatus(event.getNewStatus())
                .actorId(event.getActorId())
                .remarks(event.getRemarks())
                .build();

        statusHistoryRepository.save(history);
        log.debug("Successfully saved status history record ID: {}", history.getStatusHistoryId());
    }
}
