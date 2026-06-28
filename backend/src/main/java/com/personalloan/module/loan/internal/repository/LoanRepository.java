package com.personalloan.module.loan.internal.repository;

import com.personalloan.module.loan.internal.entity.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<LoanApplication, Long> {

    /**
     * Retrieves all loan applications associated with a specific customer ID.
     *
     * @param customerId the customer ID
     * @return the list of applications
     */
    List<LoanApplication> findByCustomerId(Long customerId);

    /**
     * Finds a loan application by its unique application number.
     *
     * @param applicationNumber the unique application number
     * @return an Optional containing the LoanApplication if found
     */
    Optional<LoanApplication> findByApplicationNumber(String applicationNumber);

    /**
     * Finds the maximum sequential application number for the current prefix to support sequence incrementation.
     */
    Optional<LoanApplication> findFirstByApplicationNumberStartingWithOrderByApplicationNumberDesc(String prefix);
}
