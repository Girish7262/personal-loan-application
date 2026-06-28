package com.personalloan.module.document.internal.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("s3StorageProvider")
@Slf4j
public class S3StorageProvider implements StorageProvider {

    @Override
    public String store(byte[] fileData, String fileName) {
        String s3Key = "s3://personal-loan-documents-bucket/" + UUID.randomUUID().toString() + "/" + fileName;
        log.info("Mock S3 storage: Uploading {} bytes of file {} to key {}", fileData.length, fileName, s3Key);
        return s3Key;
    }

    @Override
    public byte[] retrieve(String filePath) {
        log.info("Mock S3 storage: Downloading data from key {}", filePath);
        // Return dummy mock payload
        return "Mock S3 file payload content bytes".getBytes();
    }

    @Override
    public void delete(String filePath) {
        log.info("Mock S3 storage: Deleting file at key {}", filePath);
    }
}
