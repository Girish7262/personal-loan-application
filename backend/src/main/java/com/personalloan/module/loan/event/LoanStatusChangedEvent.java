package com.personalloan.module.loan.event;

import com.personalloan.module.loan.api.dto.LoanStatus;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LoanStatusChangedEvent extends ApplicationEvent {

    private final Long loanId;
    private final LoanStatus oldStatus;
    private final LoanStatus newStatus;
    private final Long actorId;
    private final String remarks;

    public LoanStatusChangedEvent(Object source, Long loanId, LoanStatus oldStatus, LoanStatus newStatus, Long actorId, String remarks) {
        super(source);
        this.loanId = loanId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.actorId = actorId;
        this.remarks = remarks;
    }
}
