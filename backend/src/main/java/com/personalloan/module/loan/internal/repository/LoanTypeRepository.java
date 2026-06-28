package com.personalloan.module.loan.internal.repository;

import com.personalloan.module.loan.internal.entity.LoanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanTypeRepository extends JpaRepository<LoanType, Long> {

    /**
     * Finds active loan configurations.
     *
     * @param isActive the active status
     * @return list of active loan types
     */
    List<LoanType> findByIsActive(boolean isActive);

    /**
     * Finds a loan configuration by its name.
     *
     * @param name the unique name
     * @return an Optional containing the LoanType if found
     */
    Optional<LoanType> findByName(String name);
}
