package com.personalloan.module.admin.internal.service;

import com.personalloan.common.event.AuditEvent;
import com.personalloan.module.admin.api.AdminFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditEventListener {

    private final AdminFacade adminFacade;

    /**
     * Intercepts application audit events published across modules and writes them to the database.
     *
     * @param event the published audit event
     */
    @EventListener
    public void handleAuditEvent(AuditEvent event) {
        log.debug("Received AuditEvent for action '{}', user ID: {}", event.getAction(), event.getUserId());
        adminFacade.logAction(
                event.getUserId(),
                event.getAction(),
                event.getResult(),
                event.getIpAddress(),
                event.getUserAgent()
        );
    }
}
