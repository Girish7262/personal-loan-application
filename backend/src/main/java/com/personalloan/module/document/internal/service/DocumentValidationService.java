package com.personalloan.module.document.internal.service;

import com.personalloan.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DocumentValidationService {

    /**
     * Validates that the physical magic bytes in the file payload match the claimed MIME content type.
     * Prevents file spoofing attacks (e.g. uploading an executable renamed to .pdf).
     *
     * @param fileData the file bytes
     * @param contentType the claimed content type (e.g. application/pdf, image/png, image/jpeg)
     */
    public void validateFileSignature(byte[] fileData, String contentType) {
        log.info("Validating magic bytes signature for claimed content type: {}", contentType);

        if (fileData == null || fileData.length < 4) {
            throw new BusinessException("Invalid file payload size. Cannot validate content signature.");
        }

        if (contentType == null) {
            throw new BusinessException("Content type header is missing.");
        }

        boolean valid = false;
        switch (contentType) {
            case "application/pdf":
                // PDF magic bytes: 0x25 0x50 0x44 0x46 (%PDF)
                valid = (fileData[0] == 0x25 && fileData[1] == 0x50 && fileData[2] == 0x44 && fileData[3] == 0x46);
                break;
            case "image/png":
                // PNG magic bytes: 0x89 0x50 0x4E 0x47
                valid = (fileData[0] == (byte) 0x89 && fileData[1] == 0x50 && fileData[2] == 0x4E && fileData[3] == 0x47);
                break;
            case "image/jpeg":
            case "image/jpg":
                // JPEG magic bytes: 0xFF 0xD8 0xFF
                valid = (fileData[0] == (byte) 0xFF && fileData[1] == (byte) 0xD8 && fileData[2] == (byte) 0xFF);
                break;
            default:
                throw new BusinessException("Unsupported MIME validation target: " + contentType);
        }

        if (!valid) {
            log.error("Magic bytes header mismatch for file type: {}. Header bytes: [{:02X} {:02X} {:02X} {:02X}]",
                    contentType, fileData[0], fileData[1], fileData[2], fileData[3]);
            throw new BusinessException("File content signature validation failed. The uploaded file payload content does not match the file extension.");
        }

        log.info("Magic bytes signature validation passed for type: {}", contentType);
    }
}
