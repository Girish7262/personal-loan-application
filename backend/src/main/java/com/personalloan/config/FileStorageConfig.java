package com.personalloan.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileStorageConfig {

    @Value("${app.file-storage.type}")
    private String storageType;

    @Value("${app.file-storage.local.base-path}")
    private String localBasePath;

    public String getStorageType() {
        return storageType;
    }

    public String getLocalBasePath() {
        return localBasePath;
    }
}
