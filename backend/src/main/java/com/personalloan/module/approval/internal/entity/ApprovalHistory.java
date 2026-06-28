package com.personalloan.module.approval.internal.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "approval_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "approval_id")
    private Long approvalId;

    @Column(name = "loan_id", nullable = false)
    private Long loanId; // Decoupled reference to LoanApplication

    @Column(name = "action", nullable = false, length = 50)
    private String action;

    @Column(name = "actor_id", nullable = false)
    private Long actorId; // Decoupled reference to Users

    @Column(name = "remarks", length = 1000)
    private String remarks;

    @Column(name = "recommended_amount", precision = 15, scale = 2)
    private BigDecimal recommendedAmount;

    @Column(name = "approved_amount", precision = 15, scale = 2)
    private BigDecimal approvedAmount;

    @Column(name = "interest_rate", precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(name = "action_date", nullable = false, updatable = false)
    private LocalDateTime actionDate;

    @PrePersist
    protected void onCreate() {
        actionDate = LocalDateTime.now();
    }
}
