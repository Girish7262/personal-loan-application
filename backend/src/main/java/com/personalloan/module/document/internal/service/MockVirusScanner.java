package com.personalloan.module.document.internal.service;

import com.personalloan.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MockVirusScanner implements VirusScanner {

    @Override
    public boolean scan(byte[] fileData, String fileName) {
        log.info("Performing mock virus scan on file: {}", fileName);

        if (fileName == null) {
            return true;
        }

        String lowerName = fileName.toLowerCase();
        if (lowerName.contains("infected") || lowerName.contains("virus") || lowerName.contains("eicar")) {
            log.error("Malicious signature detected in file name: {}", fileName);
            throw new BusinessException("Malicious file content signature detected by scanner!");
        }

        log.info("File {} is clean", fileName);
        return true;
    }
}
