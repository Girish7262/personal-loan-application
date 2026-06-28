package com.personalloan.module.auth.internal.repository;

import com.personalloan.module.auth.internal.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    @EntityGraph(attributePaths = {"role"})
    Optional<User> findByEmail(String email);
    Optional<User> findByMobileNumber(String mobileNumber);
    Optional<User> findByPasswordResetToken(String passwordResetToken);
    Optional<User> findByEmailVerificationToken(String emailVerificationToken);
    
    @EntityGraph(attributePaths = {"role"})
    Optional<User> findByRefreshTokenHash(String refreshTokenHash);

    boolean existsByEmail(String email);
    boolean existsByMobileNumber(String mobileNumber);
}
