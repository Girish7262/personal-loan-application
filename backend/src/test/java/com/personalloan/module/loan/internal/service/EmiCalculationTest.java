package com.personalloan.module.loan.internal.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmiCalculationTest {

    private final EmiCalculationService emiCalculationService = new EmiCalculationService();

    @Test
    void calculateEmi_WithStandardInputs_ShouldReturnCorrectEmiAmount() {
        // Arrange
        BigDecimal principal = BigDecimal.valueOf(100000); // 100,000 INR
        BigDecimal annualRate = BigDecimal.valueOf(12.00); // 12% p.a.
        int tenureMonths = 12; // 1 year

        // Act
        BigDecimal emi = emiCalculationService.calculateEmi(principal, annualRate, tenureMonths);

        // Assert
        // Standard amortization formula result for 100k principal, 12% rate, 12 months is 8884.88
        assertEquals(BigDecimal.valueOf(8884.88), emi);
    }

    @Test
    void calculateEmi_WithZeroRate_ShouldDivideEqually() {
        // Arrange
        BigDecimal principal = BigDecimal.valueOf(120000);
        BigDecimal annualRate = BigDecimal.ZERO;
        int tenureMonths = 12;

        // Act
        BigDecimal emi = emiCalculationService.calculateEmi(principal, annualRate, tenureMonths);

        // Assert
        assertEquals(BigDecimal.valueOf(10000.00).setScale(2), emi);
    }

    @Test
    void calculateEmi_WithInvalidInputs_ShouldReturnZero() {
        BigDecimal emi1 = emiCalculationService.calculateEmi(null, BigDecimal.valueOf(10), 12);
        BigDecimal emi2 = emiCalculationService.calculateEmi(BigDecimal.valueOf(1000), null, 12);
        BigDecimal emi3 = emiCalculationService.calculateEmi(BigDecimal.valueOf(1000), BigDecimal.valueOf(10), -5);

        assertEquals(BigDecimal.ZERO, emi1);
        assertEquals(BigDecimal.ZERO, emi2);
        assertEquals(BigDecimal.ZERO, emi3);
    }
}
