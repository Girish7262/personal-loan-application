package com.personalloan.module.auth.internal.repository;

import com.personalloan.module.auth.internal.entity.PasswordHistory;
import com.personalloan.module.auth.internal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, Long> {
    List<PasswordHistory> findByUserOrderByCreatedAtAsc(User user);
    List<PasswordHistory> findTop5ByUserOrderByCreatedAtDesc(User user);
}
