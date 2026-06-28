package com.personalloan.module.document.api;

import com.personalloan.common.dto.ApiResponse;
import com.personalloan.common.security.CustomUserDetails;
import com.personalloan.module.document.api.dto.DocumentResponse;
import com.personalloan.module.document.api.dto.DocumentType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Documents Module", description = "Endpoints for uploading, downloading, and listing loan application file attachments")
public class DocumentController {

    private final DocumentFacade documentFacade;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Upload document attachment", description = "Scans file payload for viruses, validates file type limits, and saves files outside database")
    public ResponseEntity<ApiResponse<DocumentResponse>> uploadDocument(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("file") MultipartFile file,
            @RequestParam("loanId") Long loanId,
            @RequestParam("documentType") DocumentType documentType) throws IOException {

        log.info("REST request to upload document: {} for loan ID: {} by user: {}",
                file.getOriginalFilename(), loanId, userDetails.getUsername());

        DocumentResponse response = documentFacade.uploadDocument(
                loanId,
                documentType,
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                file.getBytes(),
                userDetails.getUsername()
        );

        return ResponseEntity.ok(ApiResponse.success("Document uploaded successfully", response));
    }

    @GetMapping("/loan/{loanId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "List loan document attachments metadata", description = "Retrieves a list of all active uploaded attachments associated with a loan ID")
    public ResponseEntity<ApiResponse<List<DocumentResponse>>> getLoanDocuments(
            @PathVariable("loanId") Long loanId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        log.info("REST request to fetch document attachments for loan ID: {}", loanId);
        List<DocumentResponse> response = documentFacade.getLoanDocuments(loanId);
        return ResponseEntity.ok(ApiResponse.success("Documents list retrieved", response));
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Download physical document attachment file", description = "Downloads the binary byte payload of the uploaded attachment")
    public ResponseEntity<byte[]> downloadDocument(
            @PathVariable("id") Long documentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        log.info("REST request to download attachment ID: {} by user: {}", documentId, userDetails.getUsername());
        byte[] fileData = documentFacade.downloadDocument(documentId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"document_" + documentId + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileData);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Delete document attachment", description = "Soft-deletes metadata from database and purges binary data from storage")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(
            @PathVariable("id") Long documentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        log.info("REST request to delete document ID: {} by user: {}", documentId, userDetails.getUsername());
        documentFacade.deleteDocument(documentId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Document deleted successfully"));
    }
}
