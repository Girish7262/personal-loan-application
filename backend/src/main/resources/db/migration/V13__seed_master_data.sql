-- Seed Master Roles
INSERT INTO roles (role_code, description) VALUES
('CUSTOMER', 'Customer role for applying and tracking loans'),
('LOAN_OFFICER', 'Maker role for verification of loan applications'),
('CREDIT_MANAGER', 'Checker role for approval or rejection of loan applications'),
('FINANCE_OFFICER', 'Finance officer role for loan sanction and disbursement'),
('ADMIN', 'Administrator role for managing users, products, and reports');

-- Seed Default Loan Type
INSERT INTO loan_type (name, description, min_amount, max_amount, min_tenure_months, max_tenure_months, base_interest_rate, is_active) VALUES
('Personal Loan', 'Unsecured personal loan for individuals', 10000.00, 5000000.00, 6, 84, 12.50, 1);

-- Seed Default Admin User
INSERT INTO users (email, password_hash, mobile_number, status, role_id, created_by)
SELECT 'admin@personalloan.com', '$2b$10$SappSqVJ9JJneYMe4ZZ2aOLVZqaBhjKLY5./ZC7SLhJXxWi0uuQ/W', '9999999999', 'ACTIVE', role_id, 'SYSTEM'
FROM roles
WHERE role_code = 'ADMIN';
