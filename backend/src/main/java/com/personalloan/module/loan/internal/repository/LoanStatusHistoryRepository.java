package com.personalloan.module.loan.internal.repository;

import com.personalloan.module.loan.internal.entity.LoanStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanStatusHistoryRepository extends JpaRepository<LoanStatusHistory, Long> {

    /**
     * Finds the status history for a specific loan application, ordered by creation date descending.
     *
     * @param loanId the loan application ID
     * @return the history logs list
     */
    List<LoanStatusHistory> findByLoanIdOrderByCreatedAtDesc(Long loanId);
}
