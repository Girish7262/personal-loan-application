package com.personalloan.module.approval.internal.repository;

import com.personalloan.module.approval.internal.entity.ApprovalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalHistoryRepository extends JpaRepository<ApprovalHistory, Long> {

    /**
     * Retrieves the audit logs of status transitions for a specific loan application, ordered by date.
     */
    List<ApprovalHistory> findByLoanIdOrderByActionDateDesc(Long loanId);
}
