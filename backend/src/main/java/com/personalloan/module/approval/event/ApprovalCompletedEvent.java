package com.personalloan.module.approval.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class ApprovalCompletedEvent extends ApplicationEvent {

    private final Long loanId;
    private final String action;
    private final Long actorId;
    private final String remarks;
    private final BigDecimal approvedAmount;
    private final BigDecimal interestRate;
    private final LocalDateTime timestamp;

    public ApprovalCompletedEvent(
            Object source,
            Long loanId,
            String action,
            Long actorId,
            String remarks,
            BigDecimal approvedAmount,
            BigDecimal interestRate) {
        super(source);
        this.loanId = loanId;
        this.action = action;
        this.actorId = actorId;
        this.remarks = remarks;
        this.approvedAmount = approvedAmount;
        this.interestRate = interestRate;
        this.timestamp = LocalDateTime.now();
    }
}
