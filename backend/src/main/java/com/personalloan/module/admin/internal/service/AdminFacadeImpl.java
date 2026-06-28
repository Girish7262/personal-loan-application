package com.personalloan.module.admin.internal.service;

import com.personalloan.module.admin.api.AdminFacade;
import com.personalloan.module.admin.internal.entity.AuditLog;
import com.personalloan.module.admin.internal.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminFacadeImpl implements AdminFacade {

    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAction(Long userId, String action, String result, String ipAddress, String userAgent) {
        log.debug("Logging audit action '{}' for user ID: {}", action, userId);

        AuditLog auditLog = AuditLog.builder()
                .userId(userId)
                .action(action)
                .entity("User")
                .entityId(userId)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .result(result)
                .build();

        auditLogRepository.save(auditLog);
    }
}
