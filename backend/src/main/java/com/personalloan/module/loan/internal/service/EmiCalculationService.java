package com.personalloan.module.loan.internal.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class EmiCalculationService {

    /**
     * Calculates the Equated Monthly Installment (EMI) for a loan application.
     * Formula: EMI = [P x R x (1+R)^N] / [(1+R)^N - 1]
     * Where:
     * - P = principal amount (loan amount)
     * - R = monthly interest rate (annual interest rate / 12 / 100)
     * - N = tenure in months
     *
     * @param principal the principal loan amount
     * @param annualRate the annual interest rate (e.g. 10.5 for 10.5%)
     * @param tenureMonths the tenure in months
     * @return the calculated monthly EMI amount, scaled to 2 decimal places
     */
    public BigDecimal calculateEmi(BigDecimal principal, BigDecimal annualRate, int tenureMonths) {
        if (principal == null || annualRate == null || tenureMonths <= 0) {
            return BigDecimal.ZERO;
        }

        double p = principal.doubleValue();
        double annualRatePercent = annualRate.doubleValue();
        double r = annualRatePercent / 12.0 / 100.0;
        double n = tenureMonths;

        if (r == 0.0) {
            // Flat division for zero interest rate
            return principal.divide(BigDecimal.valueOf(tenureMonths), 2, RoundingMode.HALF_UP);
        }

        double emiDouble = (p * r * Math.pow(1.0 + r, n)) / (Math.pow(1.0 + r, n) - 1.0);
        return BigDecimal.valueOf(emiDouble).setScale(2, RoundingMode.HALF_UP);
    }
}
