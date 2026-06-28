package com.personalloan.module.document.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {
    private Long documentId;
    private Long loanId;
    private String fileName;
    private Long fileSize;
    private String fileType;
    private String documentType;
    private String downloadUrl;
    private LocalDateTime createdAt;
    private String createdBy;
}
