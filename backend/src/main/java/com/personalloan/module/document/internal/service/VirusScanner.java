package com.personalloan.module.document.internal.service;

public interface VirusScanner {

    /**
     * Scans a file payload for viruses or malicious signatures.
     *
     * @param fileData the raw byte contents of the file
     * @param fileName the name of the file
     * @return true if the file is clean and safe to store
     * @throws com.personalloan.common.exception.BusinessException if a threat is detected
     */
    boolean scan(byte[] fileData, String fileName);
}
