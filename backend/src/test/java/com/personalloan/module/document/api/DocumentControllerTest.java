package com.personalloan.module.document.api;

import com.personalloan.common.security.*;
import com.personalloan.module.document.api.dto.DocumentResponse;
import com.personalloan.module.document.api.dto.DocumentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.cors.CorsConfigurationSource;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DocumentController.class)
@AutoConfigureMockMvc(addFilters = false) // Bypass spring security authentication gates
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentFacade documentFacade;

    // Satisfy security context loading constraints
    @MockBean private CorsConfigurationSource corsConfigurationSource;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean private RateLimiterFilter rateLimiterFilter;
    @MockBean private MdcLoggingFilter mdcLoggingFilter;
    @MockBean private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @MockBean private CustomAccessDeniedHandler customAccessDeniedHandler;

    private CustomUserDetails testPrincipal;
    private DocumentResponse documentResponse;

    @BeforeEach
    void setUp() {
        testPrincipal = new CustomUserDetails(
                100L,
                "girish@gmail.com",
                "PASSWORD_HASH",
                Collections.singletonList(() -> "ROLE_CUSTOMER"),
                "ACTIVE"
        );

        documentResponse = DocumentResponse.builder()
                .documentId(10L)
                .loanId(1L)
                .fileName("kyc.pdf")
                .fileSize(100L)
                .fileType("application/pdf")
                .documentType("AADHAAR")
                .downloadUrl("/api/v1/documents/10/download")
                .createdAt(LocalDateTime.now())
                .createdBy("girish@gmail.com")
                .build();
    }

    @Test
    void uploadDocument_ShouldReturnUploadedMetadata() throws Exception {
        // Arrange
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "kyc.pdf",
                "application/pdf",
                new byte[]{0x25, 0x50, 0x44, 0x46}
        );

        when(documentFacade.uploadDocument(
                eq(1L),
                eq(DocumentType.AADHAAR),
                eq("kyc.pdf"),
                eq("application/pdf"),
                eq(4L),
                any(byte[].class),
                eq("girish@gmail.com")
        )).thenReturn(documentResponse);

        // Act & Assert
        mockMvc.perform(multipart("/api/v1/documents/upload")
                        .file(mockFile)
                        .param("loanId", "1")
                        .param("documentType", "AADHAAR")
                        .with(user(testPrincipal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Document uploaded successfully"))
                .andExpect(jsonPath("$.data.documentId").value(10))
                .andExpect(jsonPath("$.data.fileName").value("kyc.pdf"));
    }

    @Test
    void getLoanDocuments_ShouldReturnList() throws Exception {
        // Arrange
        when(documentFacade.getLoanDocuments(1L)).thenReturn(Collections.singletonList(documentResponse));

        // Act & Assert
        mockMvc.perform(get("/api/v1/documents/loan/1")
                        .with(user(testPrincipal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].documentId").value(10));
    }

    @Test
    void downloadDocument_ShouldReturnByteStream() throws Exception {
        // Arrange
        byte[] rawBytes = new byte[]{1, 2, 3, 4};
        when(documentFacade.downloadDocument(10L, 100L)).thenReturn(rawBytes);

        // Act & Assert
        mockMvc.perform(get("/api/v1/documents/10/download")
                        .with(user(testPrincipal)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(content().bytes(rawBytes));
    }

    @Test
    void deleteDocument_ShouldReturnSuccess() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/v1/documents/10")
                        .with(user(testPrincipal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Document deleted successfully"));
    }
}
