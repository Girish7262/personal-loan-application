package com.personalloan.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminUserInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@personalloan.com}")
    private String adminEmail;

    @Value("${app.admin.password:}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        // 1. Check if ADMIN user already exists in the database
        String checkAdminSql = "SELECT COUNT(*) FROM users u JOIN roles r ON u.role_id = r.role_id WHERE r.role_code = 'ADMIN'";
        Integer adminCount = jdbcTemplate.queryForObject(checkAdminSql, Integer.class);

        if (adminCount != null && adminCount == 0) {
            log.info("No admin user found. Initializing default admin user...");

            // 2. Fetch ADMIN role_id
            String fetchRoleIdSql = "SELECT role_id FROM roles WHERE role_code = 'ADMIN'";
            List<Long> roleIds = jdbcTemplate.query(fetchRoleIdSql, (rs, rowNum) -> rs.getLong("role_id"));
            if (roleIds.isEmpty()) {
                log.error("ADMIN role not found in database. Seed data V13__seed_master_data.sql might not have run correctly.");
                return;
            }
            Long adminRoleId = roleIds.get(0);

            // 3. Determine password and force change flag
            String passwordToUse;
            boolean forcePasswordChange;

            if (adminPassword != null && !adminPassword.trim().isEmpty()) {
                passwordToUse = adminPassword;
                forcePasswordChange = false;
                log.info("Admin password loaded from configuration/environment variables.");
            } else {
                passwordToUse = generateRandomPassword();
                forcePasswordChange = true;
                log.warn("\n" +
                        "========================================================================\n" +
                        "SECURITY WARNING: No app.admin.password configured in environment!\n" +
                        "A temporary random admin password has been generated for first startup.\n" +
                        "Email: {}\n" +
                        "Temporary Password: {}\n" +
                        "You will be required to change this password on your first login.\n" +
                        "========================================================================",
                        adminEmail, passwordToUse);
            }

            // 4. Hash password and insert user
            String hashedPassword = passwordEncoder.encode(passwordToUse);
            String insertUserSql = "INSERT INTO users (email, password_hash, mobile_number, status, role_id, created_by, force_password_change) VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            jdbcTemplate.update(insertUserSql, 
                    adminEmail, 
                    hashedPassword, 
                    "9999999999", 
                    "ACTIVE", 
                    adminRoleId, 
                    "SYSTEM", 
                    forcePasswordChange ? 1 : 0);
            
            log.info("Default admin user created successfully.");
        }
    }

    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
