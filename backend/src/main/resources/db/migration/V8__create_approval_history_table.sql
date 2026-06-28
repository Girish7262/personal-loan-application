CREATE TABLE approval_history (
    approval_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    loan_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL,
    actor_id BIGINT NOT NULL,
    remarks VARCHAR(1000) NULL,
    recommended_amount DECIMAL(15, 2) NULL,
    approved_amount DECIMAL(15, 2) NULL,
    interest_rate DECIMAL(5, 2) NULL,
    action_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_approval_history_loan_application FOREIGN KEY (loan_id) REFERENCES loan_application(loan_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_approval_history_users FOREIGN KEY (actor_id) REFERENCES users(user_id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
