package com.personalloan.module.document.internal.service;

public interface StorageProvider {

    /**
     * Stores the physical file content and returns its target path/identifier.
     *
     * @param fileData the file bytes
     * @param fileName the original file name to store
     * @return the saved filePath or S3 key identifier
     */
    String store(byte[] fileData, String fileName);

    /**
     * Retrieves the physical bytes of the file.
     *
     * @param filePath the path or key where the file resides
     * @return the file bytes
     */
    byte[] retrieve(String filePath);

    /**
     * Deletes the physical file from the storage system.
     *
     * @param filePath the path or key to delete
     */
    void delete(String filePath);
}
