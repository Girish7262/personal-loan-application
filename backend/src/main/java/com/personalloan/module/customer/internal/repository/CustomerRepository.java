package com.personalloan.module.customer.internal.repository;

import com.personalloan.module.customer.internal.entity.CustomerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerProfile, Long> {

    /**
     * Finds a customer profile associated with a user ID.
     *
     * @param userId the user ID
     * @return an Optional containing the CustomerProfile if found
     */
    Optional<CustomerProfile> findByUserId(Long userId);

    /**
     * Finds a customer profile by PAN number.
     *
     * @param panNumber the PAN number
     * @return an Optional containing the CustomerProfile if found
     */
    Optional<CustomerProfile> findByPanNumber(String panNumber);

    /**
     * Finds a customer profile by encrypted Aadhaar number.
     *
     * @param aadhaarNumber the encrypted Aadhaar representation
     * @return an Optional containing the CustomerProfile if found
     */
    Optional<CustomerProfile> findByAadhaarNumber(String aadhaarNumber);
}
