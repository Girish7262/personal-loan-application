package com.personalloan.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AuditEvent extends ApplicationEvent {

    private final Long userId;
    private final String action;
    private final String result;
    private final String ipAddress;
    private final String userAgent;

    public AuditEvent(Object source, Long userId, String action, String result, String ipAddress, String userAgent) {
        super(source);
        this.userId = userId;
        this.action = action;
        this.result = result;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }
}
