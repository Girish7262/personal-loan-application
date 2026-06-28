CREATE TABLE loan_status_history (
    status_history_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    loan_id BIGINT NOT NULL,
    from_status VARCHAR(30) NULL,
    to_status VARCHAR(30) NOT NULL,
    actor_id BIGINT NOT NULL,
    remarks VARCHAR(1000) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_loan_status_history_loan_application FOREIGN KEY (loan_id) REFERENCES loan_application(loan_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_loan_status_history_users FOREIGN KEY (actor_id) REFERENCES users(user_id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
