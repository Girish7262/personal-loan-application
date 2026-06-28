package com.personalloan.module.loan.internal.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Service
public class EmiCalculationService {

    private static final MathContext MC = new MathContext(16, RoundingMode.HALF_UP);

    /**
     * Calculates the Equated Monthly Installment (EMI) using strictly BigDecimal.
     * Formula: EMI = [P x R x (1+R)^N] / [(1+R)^N - 1]
     * Where:
     * - P = principal amount (loan amount)
     * - R = monthly interest rate (annual interest rate / 12 / 100)
     * - N = tenure in months
     */
    public BigDecimal calculateEmi(BigDecimal principal, BigDecimal annualRate, int tenureMonths) {
        if (principal == null || annualRate == null || tenureMonths <= 0) {
            return BigDecimal.ZERO;
        }

        if (annualRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(BigDecimal.valueOf(tenureMonths), 2, RoundingMode.HALF_UP);
        }

        // R = Annual Rate / 12 / 100 = Annual Rate / 1200
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);

        // (1 + R)
        BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate);

        // (1 + R)^N
        BigDecimal onePlusRPowN = onePlusR.pow(tenureMonths, MC);

        // P * R * (1 + R)^N
        BigDecimal numerator = principal.multiply(monthlyRate, MC).multiply(onePlusRPowN, MC);

        // (1 + R)^N - 1
        BigDecimal denominator = onePlusRPowN.subtract(BigDecimal.ONE);

        if (denominator.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        // EMI = numerator / denominator
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }
}
