package com.personalloan.module.loan.api;

import com.personalloan.common.dto.ApiResponse;
import com.personalloan.common.security.CustomUserDetails;
import com.personalloan.module.loan.api.dto.EligibilityResult;
import com.personalloan.module.loan.api.dto.LoanApplicationRequest;
import com.personalloan.module.loan.api.dto.LoanApplicationResponse;
import com.personalloan.module.loan.api.dto.LoanStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Loan Applications", description = "Endpoints for creating, checking, and managing personal loan requests")
public class LoanController {

    private final LoanFacade loanFacade;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Submit a new loan application", description = "Performs calculations and validations, then saves request with SUBMITTED status")
    public ResponseEntity<ApiResponse<LoanApplicationResponse>> submitApplication(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody LoanApplicationRequest request) {
        log.info("REST request to submit loan application for user: {}", userDetails.getUsername());
        LoanApplicationResponse response = loanFacade.submitApplication(
                userDetails.getUserId(),
                request,
                userDetails.getUsername()
        );
        return ResponseEntity.ok(ApiResponse.success("Loan application submitted successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Retrieve specific loan details", description = "Fetches a loan application by database ID, validating ownership")
    public ResponseEntity<ApiResponse<LoanApplicationResponse>> getApplication(
            @PathVariable("id") Long loanId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("REST request to fetch loan details ID: {}", loanId);
        LoanApplicationResponse response = loanFacade.getApplication(loanId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Loan details retrieved", response));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "List customer's applications", description = "Fetches all loan applications submitted by the current user profile")
    public ResponseEntity<ApiResponse<List<LoanApplicationResponse>>> getMyApplications(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("REST request to fetch my applications list for user: {}", userDetails.getUsername());
        List<LoanApplicationResponse> response = loanFacade.getApplications(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Applications list retrieved", response));
    }

    @GetMapping("/eligibility")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Pre-evaluate eligibility dynamically", description = "Performs inline checking of FOIR, profile compliance, and active limits without committing request rows")
    public ResponseEntity<ApiResponse<EligibilityResult>> checkEligibility(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("loanTypeId") Long loanTypeId,
            @RequestParam("loanAmount") BigDecimal loanAmount,
            @RequestParam("loanTenureMonths") Integer loanTenureMonths,
            @RequestParam("monthlyIncome") BigDecimal monthlyIncome,
            @RequestParam("existingEmis") BigDecimal existingEmis) {
        log.info("REST request to pre-evaluate eligibility criteria for user: {}", userDetails.getUsername());

        LoanApplicationRequest request = LoanApplicationRequest.builder()
                .loanTypeId(loanTypeId)
                .loanAmount(loanAmount)
                .loanTenureMonths(loanTenureMonths)
                .monthlyIncome(monthlyIncome)
                .existingEmis(existingEmis)
                .build();

        EligibilityResult result = loanFacade.checkEligibility(userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("Eligibility calculation completed", result));
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Progress draft application to submitted state", description = "Transitions an application from DRAFT status to SUBMITTED, starting validation cycles")
    public ResponseEntity<ApiResponse<LoanApplicationResponse>> submitDraftLoan(
            @PathVariable("id") Long loanId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("REST request to transition draft application to submitted ID: {}", loanId);
        LoanApplicationResponse response = loanFacade.updateApplicationStatus(
                loanId,
                LoanStatus.SUBMITTED,
                userDetails.getUserId(),
                userDetails.getUsername()
        );
        return ResponseEntity.ok(ApiResponse.success("Loan application transitioned to SUBMITTED status", response));
    }
}
