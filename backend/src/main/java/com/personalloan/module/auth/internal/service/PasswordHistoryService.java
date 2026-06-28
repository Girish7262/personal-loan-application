package com.personalloan.module.auth.internal.service;

import com.personalloan.common.exception.BusinessException;
import com.personalloan.module.auth.internal.entity.PasswordHistory;
import com.personalloan.module.auth.internal.entity.User;
import com.personalloan.module.auth.internal.repository.PasswordHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordHistoryService {

    private final PasswordHistoryRepository passwordHistoryRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Checks if a new password matches the user's current password or any of their last 5 passwords.
     *
     * @param user the user changing their password
     * @param rawPassword the new password in plain text
     * @throws BusinessException if the password matches any of the last 5 passwords or the current password
     */
    @Transactional(readOnly = true)
    public void validatePasswordNotUsed(User user, String rawPassword) {
        // 1. Check against current active password
        if (passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new BusinessException("New password cannot be the same as your current password");
        }

        // 2. Check against last 5 historical passwords
        List<PasswordHistory> history = passwordHistoryRepository.findTop5ByUserOrderByCreatedAtDesc(user);
        for (PasswordHistory record : history) {
            if (passwordEncoder.matches(rawPassword, record.getPasswordHash())) {
                throw new BusinessException("New password cannot be the same as any of your last 5 passwords");
            }
        }
    }

    /**
     * Records the user's current password hash in the history logs. If the log count exceeds 5,
     * deletes the oldest entries to maintain a strict maximum history window of 5.
     *
     * @param user the user whose password was changed
     * @param passwordHash the newly generated encoded password hash
     */
    @Transactional
    public void recordPasswordChange(User user, String passwordHash) {
        // Insert new password history entry
        PasswordHistory newRecord = PasswordHistory.builder()
                .user(user)
                .passwordHash(passwordHash)
                .build();
        passwordHistoryRepository.save(newRecord);

        // Keep only the last 5 entries
        List<PasswordHistory> history = passwordHistoryRepository.findByUserOrderByCreatedAtAsc(user);
        if (history.size() > 5) {
            int toDelete = history.size() - 5;
            for (int i = 0; i < toDelete; i++) {
                passwordHistoryRepository.delete(history.get(i));
            }
            log.info("Pruned {} old password history records for user: {}", toDelete, user.getEmail());
        }
    }
}
