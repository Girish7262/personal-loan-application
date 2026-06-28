package com.personalloan.common.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LoggingEmailService implements EmailService {

    @Override
    public void sendEmail(String to, String subject, String body) {
        log.info("\n" +
                "========================================================================\n" +
                "SIMULATED EMAIL LOG\n" +
                "To: {}\n" +
                "Subject: {}\n" +
                "------------------------------------------------------------------------\n" +
                "{}\n" +
                "========================================================================",
                to, subject, body);
    }
}
