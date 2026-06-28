package com.personalloan.module.document.internal.service;

import com.personalloan.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service("localStorageProvider")
@Slf4j
public class LocalStorageProvider implements StorageProvider {

    private final Path rootPath = Paths.get("uploads");

    public LocalStorageProvider() {
        try {
            if (!Files.exists(rootPath)) {
                Files.createDirectories(rootPath);
            }
        } catch (IOException e) {
            log.error("Failed to initialize uploads root directory", e);
            throw new RuntimeException("Could not initialize local upload folder", e);
        }
    }

    @Override
    public String store(byte[] fileData, String fileName) {
        log.info("Storing file {} to local storage", fileName);
        String uniqueName = UUID.randomUUID().toString() + "_" + fileName;
        Path targetPath = rootPath.resolve(uniqueName);
        try {
            Files.write(targetPath, fileData);
            return targetPath.toAbsolutePath().toString();
        } catch (IOException e) {
            log.error("Failed to write local file data", e);
            throw new BusinessException("Local file write failure: " + e.getMessage());
        }
    }

    @Override
    public byte[] retrieve(String filePath) {
        log.info("Retrieving file from local storage path: {}", filePath);
        try {
            Path targetPath = Paths.get(filePath);
            if (!Files.exists(targetPath)) {
                throw new BusinessException("Requested physical file not found on disk");
            }
            return Files.readAllBytes(targetPath);
        } catch (IOException e) {
            log.error("Failed to read local file data", e);
            throw new BusinessException("Could not read local file: " + e.getMessage());
        }
    }

    @Override
    public void delete(String filePath) {
        log.info("Deleting file from local storage path: {}", filePath);
        try {
            Path targetPath = Paths.get(filePath);
            Files.deleteIfExists(targetPath);
        } catch (IOException e) {
            log.error("Failed to delete local file", e);
            throw new BusinessException("Could not delete local file: " + e.getMessage());
        }
    }
}
