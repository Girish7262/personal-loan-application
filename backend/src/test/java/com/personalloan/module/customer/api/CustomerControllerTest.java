package com.personalloan.module.customer.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personalloan.common.security.CustomAccessDeniedHandler;
import com.personalloan.common.security.CustomUserDetails;
import com.personalloan.common.security.JwtAuthenticationEntryPoint;
import com.personalloan.common.security.JwtAuthenticationFilter;
import com.personalloan.common.security.RateLimiterFilter;
import com.personalloan.common.security.MdcLoggingFilter;
import com.personalloan.module.customer.api.dto.*;
import com.personalloan.module.customer.api.CustomerFacade;
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
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CustomerController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable spring security filters to avoid auth setup complexity
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerFacade customerFacade;

    // Mock security dependencies to satisfy context loading constraints
    @MockBean
    private CorsConfigurationSource corsConfigurationSource;
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean
    private RateLimiterFilter rateLimiterFilter;
    @MockBean
    private MdcLoggingFilter mdcLoggingFilter;
    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @MockBean
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    private CustomUserDetails testPrincipal;
    private CustomerProfileRequest request;
    private CustomerProfileResponse response;

    @BeforeEach
    void setUp() {
        testPrincipal = new CustomUserDetails(
                100L,
                "girish@gmail.com",
                "PASSWORD_HASH",
                Collections.singletonList(() -> "ROLE_CUSTOMER"),
                "ACTIVE"
        );

        request = CustomerProfileRequest.builder()
                .firstName("Girish")
                .lastName("Patil")
                .dateOfBirth(LocalDate.of(1995, 5, 10))
                .gender("MALE")
                .fatherName("Ramesh Patil")
                .mobileNumber("9876543210")
                .email("girish@gmail.com")
                .panNumber("ABCDE1234F")
                .aadhaarNumber("123412341234")
                .street("Main Street")
                .city("Pune")
                .state("Maharashtra")
                .pincode("411001")
                .employmentType("SALARIED")
                .monthlyIncome(BigDecimal.valueOf(50000))
                .build();

        response = CustomerProfileResponse.builder()
                .customerId(1L)
                .userId(100L)
                .firstName("Girish")
                .lastName("Patil")
                .panNumber("XXXXXX1234")
                .aadhaarNumber("XXXXXXXX1234")
                .profileStatus("COMPLETE")
                .build();
    }

    @Test
    void getProfile_ShouldReturnProfileResponse() throws Exception {
        // Arrange
        when(customerFacade.getProfile(100L)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/v1/customer/profile")
                        .with(user(testPrincipal))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Profile details retrieved"))
                .andExpect(jsonPath("$.data.userId").value(100))
                .andExpect(jsonPath("$.data.panNumber").value("XXXXXX1234"))
                .andExpect(jsonPath("$.data.aadhaarNumber").value("XXXXXXXX1234"));
    }

    @Test
    void createProfile_ShouldReturnCreatedProfileResponse() throws Exception {
        // Arrange
        when(customerFacade.createProfile(eq(100L), any(CustomerProfileRequest.class), eq("girish@gmail.com")))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/customer/profile")
                        .with(user(testPrincipal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Profile created successfully"))
                .andExpect(jsonPath("$.data.profileStatus").value("COMPLETE"));
    }

    @Test
    void updateProfile_ShouldReturnUpdatedProfileResponse() throws Exception {
        // Arrange
        when(customerFacade.updateProfile(eq(100L), any(CustomerProfileRequest.class), eq("girish@gmail.com")))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/api/v1/customer/profile")
                        .with(user(testPrincipal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Profile updated successfully"));
    }

    @Test
    void patchProfile_ShouldReturnPatchedProfileResponse() throws Exception {
        // Arrange
        CustomerProfileRequest patchRequest = CustomerProfileRequest.builder()
                .city("Mumbai")
                .build();
        when(customerFacade.patchProfile(eq(100L), any(CustomerProfileRequest.class), eq("girish@gmail.com")))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/customer/profile")
                        .with(user(testPrincipal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Profile patched successfully"));
    }

    @Test
    void getProfileCompletion_ShouldReturnCompletionDetails() throws Exception {
        // Arrange
        ProfileCompletionResponse completion = new ProfileCompletionResponse(
                75,
                ProfileStatus.PARTIAL,
                List.of("companyName", "monthlyIncome")
        );
        when(customerFacade.getProfileCompletion(100L)).thenReturn(completion);

        // Act & Assert
        mockMvc.perform(get("/api/v1/customer/profile/completion")
                        .with(user(testPrincipal))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.percentage").value(75))
                .andExpect(jsonPath("$.data.status").value("PARTIAL"))
                .andExpect(jsonPath("$.data.missingFields[0]").value("companyName"));
    }
}
