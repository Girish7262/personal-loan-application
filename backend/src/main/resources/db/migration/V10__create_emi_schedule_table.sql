CREATE TABLE emi_schedule (
    emi_schedule_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    loan_id BIGINT NOT NULL,
    total_emis INT NOT NULL,
    monthly_emi DECIMAL(15, 2) NOT NULL,
    installments JSON NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NULL,
    updated_by VARCHAR(100) NULL,
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    CONSTRAINT uk_emi_schedule_loan_id UNIQUE (loan_id),
    CONSTRAINT fk_emi_schedule_loan_application FOREIGN KEY (loan_id) REFERENCES loan_application(loan_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
