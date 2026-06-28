package com.personalloan.module.loan.internal.entity;

import com.personalloan.module.loan.api.dto.LoanStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_application")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loan_id")
    private Long loanId;

    @Column(name = "customer_id", nullable = false)
    private Long customerId; // Decoupled reference to CustomerProfile (maps to customer_id)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_type_id", nullable = false)
    private LoanType loanType; // Intra-module relation

    @Column(name = "application_number", nullable = false, unique = true, length = 50)
    private String applicationNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "loan_status", nullable = false, length = 30)
    private LoanStatus loanStatus;

    @Column(name = "loan_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal loanAmount;

    @Column(name = "approved_amount", precision = 15, scale = 2)
    private BigDecimal approvedAmount;

    @Column(name = "loan_tenure_months", nullable = false)
    private Integer loanTenureMonths;

    @Column(name = "interest_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(name = "purpose", nullable = false, length = 500)
    private String purpose;

    @Column(name = "monthly_income", nullable = false, precision = 15, scale = 2)
    private BigDecimal monthlyIncome;

    @Column(name = "existing_emis", nullable = false, precision = 15, scale = 2)
    private BigDecimal existingEmis;

    @Column(name = "emi", nullable = false, precision = 15, scale = 2)
    private BigDecimal emi;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isDeleted == null) {
            isDeleted = false;
        }
        if (loanStatus == null) {
            loanStatus = LoanStatus.DRAFT;
        }
        if (existingEmis == null) {
            existingEmis = BigDecimal.ZERO;
        }
        if (emi == null) {
            emi = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
