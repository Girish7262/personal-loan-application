package com.personalloan.module.auth.repository;

import com.personalloan.module.auth.entity.PasswordHistory;
import com.personalloan.module.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, Long> {
    List<PasswordHistory> findByUserOrderByCreatedAtAsc(User user);
    List<PasswordHistory> findTop5ByUserOrderByCreatedAtDesc(User user);
}
