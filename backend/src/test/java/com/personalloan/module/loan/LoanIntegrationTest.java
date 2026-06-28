package com.personalloan.module.loan;

import com.personalloan.BaseIntegrationTest;
import com.personalloan.module.loan.api.dto.LoanPurpose;
import com.personalloan.module.loan.api.dto.LoanStatus;
import com.personalloan.module.loan.internal.entity.LoanApplication;
import com.personalloan.module.loan.internal.entity.LoanStatusHistory;
import com.personalloan.module.loan.internal.entity.LoanType;
import com.personalloan.module.loan.internal.repository.LoanRepository;
import com.personalloan.module.loan.internal.repository.LoanStatusHistoryRepository;
import com.personalloan.module.loan.internal.repository.LoanTypeRepository;
import com.personalloan.module.loan.event.LoanStatusChangedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoanIntegrationTest extends BaseIntegrationTest {

    @Autowired private LoanRepository loanRepository;
    @Autowired private LoanTypeRepository loanTypeRepository;
    @Autowired private LoanStatusHistoryRepository loanStatusHistoryRepository;
    @Autowired private ApplicationEventPublisher eventPublisher;

    private LoanType activeLoanType;

    @BeforeEach
    void cleanDatabase() {
        loanStatusHistoryRepository.deleteAll();
        loanRepository.deleteAll();
        loanTypeRepository.deleteAll();

        // Seed a sample LoanType for references
        LoanType type = LoanType.builder()
                .name("Standard Personal Loan")
                .description("Sample Product")
                .minAmount(BigDecimal.valueOf(10000))
                .maxAmount(BigDecimal.valueOf(200000))
                .minTenureMonths(6)
                .maxTenureMonths(24)
                .baseInterestRate(BigDecimal.valueOf(11.50))
                .foirPercentage(BigDecimal.valueOf(50.00))
                .isActive(true)
                .build();
        activeLoanType = loanTypeRepository.saveAndFlush(type);
    }

    @Test
    void saveApplication_ShouldPersistCorrectly() {
        // Arrange
        LoanApplication application = LoanApplication.builder()
                .customerId(99L)
                .loanType(activeLoanType)
                .applicationNumber("PL-2026-99999999")
                .loanStatus(LoanStatus.SUBMITTED)
                .loanAmount(BigDecimal.valueOf(50000))
                .loanTenureMonths(12)
                .interestRate(BigDecimal.valueOf(11.50))
                .purpose(LoanPurpose.MARRIAGE)
                .monthlyIncome(BigDecimal.valueOf(80000))
                .existingEmis(BigDecimal.ZERO)
                .emi(BigDecimal.valueOf(4432.10))
                .submittedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        // Act
        LoanApplication saved = loanRepository.saveAndFlush(application);

        // Assert
        assertNotNull(saved.getLoanId());
        assertEquals(0, saved.getVersion()); // Optimistic lock counter should start at 0
    }

    @Test
    void statusChangedEvent_ShouldTriggerListenerAndInsertStatusHistoryLog() throws InterruptedException {
        // Arrange
        LoanApplication application = LoanApplication.builder()
                .customerId(99L)
                .loanType(activeLoanType)
                .applicationNumber("PL-2026-11112222")
                .loanStatus(LoanStatus.SUBMITTED)
                .loanAmount(BigDecimal.valueOf(50000))
                .loanTenureMonths(12)
                .interestRate(BigDecimal.valueOf(11.50))
                .purpose(LoanPurpose.MARRIAGE)
                .monthlyIncome(BigDecimal.valueOf(80000))
                .existingEmis(BigDecimal.ZERO)
                .emi(BigDecimal.valueOf(4432.10))
                .isDeleted(false)
                .build();
        LoanApplication saved = loanRepository.saveAndFlush(application);

        // Act - Publish LoanStatusChangedEvent directly
        eventPublisher.publishEvent(new LoanStatusChangedEvent(
                this,
                saved.getLoanId(),
                null,
                LoanStatus.SUBMITTED,
                200L, // Actor User ID
                "Integration test logs history check"
        ));

        // Let Spring execute sync transaction listeners
        List<LoanStatusHistory> histories = loanStatusHistoryRepository.findByLoanIdOrderByCreatedAtDesc(saved.getLoanId());

        // Assert
        assertFalse(histories.isEmpty());
        LoanStatusHistory historyLog = histories.get(0);
        assertEquals(saved.getLoanId(), historyLog.getLoanId());
        assertNull(historyLog.getFromStatus());
        assertEquals(LoanStatus.SUBMITTED, historyLog.getToStatus());
        assertEquals(200L, historyLog.getActorId());
        assertEquals("Integration test logs history check", historyLog.getRemarks());
    }

    @Test
    void optimisticLocking_OnLoanApplication_ShouldRaiseConcurrencyFailureException() {
        // Arrange
        LoanApplication application = LoanApplication.builder()
                .customerId(99L)
                .loanType(activeLoanType)
                .applicationNumber("PL-2026-77778888")
                .loanStatus(LoanStatus.DRAFT)
                .loanAmount(BigDecimal.valueOf(50000))
                .loanTenureMonths(12)
                .interestRate(BigDecimal.valueOf(11.50))
                .purpose(LoanPurpose.TRAVEL)
                .monthlyIncome(BigDecimal.valueOf(80000))
                .existingEmis(BigDecimal.ZERO)
                .emi(BigDecimal.valueOf(4432.10))
                .isDeleted(false)
                .build();
        LoanApplication saved = loanRepository.saveAndFlush(application);
        assertEquals(0, saved.getVersion());

        // Act - Load two separate instances of the same database row
        LoanApplication inst1 = loanRepository.findById(saved.getLoanId()).orElseThrow();
        LoanApplication inst2 = loanRepository.findById(saved.getLoanId()).orElseThrow();

        // Save inst1
        inst1.setLoanStatus(LoanStatus.SUBMITTED);
        loanRepository.saveAndFlush(inst1); // Increments version to 1 in DB

        // Modify inst2 (still holds version = 0) and attempt save
        inst2.setLoanStatus(LoanStatus.CANCELLED);

        // Assert - Saving inst2 must raise concurrency lock error
        assertThrows(ObjectOptimisticLockingFailureException.class, () -> loanRepository.saveAndFlush(inst2));
    }
}
