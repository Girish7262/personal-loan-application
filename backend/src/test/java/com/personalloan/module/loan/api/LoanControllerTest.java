package com.personalloan.module.loan.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personalloan.common.security.*;
import com.personalloan.module.loan.api.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.cors.CorsConfigurationSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LoanController.class)
@AutoConfigureMockMvc(addFilters = false) // Bypass spring security authentication gates
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LoanFacade loanFacade;

    // Satisfy security context loading constraints
    @MockBean private CorsConfigurationSource corsConfigurationSource;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean private RateLimiterFilter rateLimiterFilter;
    @MockBean private MdcLoggingFilter mdcLoggingFilter;
    @MockBean private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @MockBean private CustomAccessDeniedHandler customAccessDeniedHandler;

    private CustomUserDetails testPrincipal;
    private LoanApplicationRequest request;
    private LoanApplicationResponse response;

    @BeforeEach
    void setUp() {
        testPrincipal = new CustomUserDetails(
                100L,
                "girish@gmail.com",
                "PASSWORD_HASH",
                Collections.singletonList(() -> "ROLE_CUSTOMER"),
                "ACTIVE"
        );

        request = LoanApplicationRequest.builder()
                .loanTypeId(1L)
                .loanAmount(BigDecimal.valueOf(100000))
                .loanTenureMonths(12)
                .purpose(LoanPurpose.MEDICAL)
                .monthlyIncome(BigDecimal.valueOf(100000))
                .existingEmis(BigDecimal.valueOf(10000))
                .build();

        LoanTypeResponse typeResponse = LoanTypeResponse.builder()
                .loanTypeId(1L)
                .name("Personal Loan")
                .isActive(true)
                .foirPercentage(BigDecimal.valueOf(50.00))
                .build();

        response = LoanApplicationResponse.builder()
                .loanId(1L)
                .customerId(10L)
                .loanType(typeResponse)
                .applicationNumber("PL-2026-00000001")
                .loanStatus("SUBMITTED")
                .loanAmount(BigDecimal.valueOf(100000))
                .loanTenureMonths(12)
                .interestRate(BigDecimal.valueOf(12.00))
                .purpose(LoanPurpose.MEDICAL.name())
                .monthlyIncome(BigDecimal.valueOf(100000))
                .existingEmis(BigDecimal.valueOf(10000))
                .emi(BigDecimal.valueOf(8884.88))
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void submitApplication_ShouldReturnSavedLoanResponse() throws Exception {
        // Arrange
        when(loanFacade.submitApplication(eq(100L), any(LoanApplicationRequest.class), eq("girish@gmail.com")))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/loans")
                        .with(user(testPrincipal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Loan application submitted successfully"))
                .andExpect(jsonPath("$.data.applicationNumber").value("PL-2026-00000001"))
                .andExpect(jsonPath("$.data.loanStatus").value("SUBMITTED"));
    }

    @Test
    void getApplication_ShouldReturnLoanDetails() throws Exception {
        // Arrange
        when(loanFacade.getApplication(1L, 100L)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/v1/loans/1")
                        .with(user(testPrincipal))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.loanId").value(1));
    }

    @Test
    void checkEligibility_ShouldReturnEligibilityResult() throws Exception {
        // Arrange
        EligibilityResult eligibility = new EligibilityResult(true, Collections.emptyList(), BigDecimal.valueOf(500000), BigDecimal.valueOf(18.00));
        when(loanFacade.checkEligibility(eq(100L), any(LoanApplicationRequest.class))).thenReturn(eligibility);

        // Act & Assert
        mockMvc.perform(get("/api/v1/loans/eligibility")
                        .with(user(testPrincipal))
                        .param("loanTypeId", "1")
                        .param("loanAmount", "100000")
                        .param("loanTenureMonths", "12")
                        .param("monthlyIncome", "100000")
                        .param("existingEmis", "10000")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.eligible").value(true))
                .andExpect(jsonPath("$.data.foir").value(18.00));
    }

    @Test
    void submitDraftLoan_ShouldTransitionStatusToSubmitted() throws Exception {
        // Arrange
        when(loanFacade.updateApplicationStatus(1L, LoanStatus.SUBMITTED, 100L, "girish@gmail.com"))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/loans/1/submit")
                        .with(user(testPrincipal))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Loan application transitioned to SUBMITTED status"));
    }
}
