package com.personalloan.module.customer.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerProfileResponse {

    private Long customerId;
    private Long userId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    private String fatherName;
    private String mobileNumber;
    private String email;
    private String panNumber;
    private String aadhaarNumber;
    private String street;
    private String city;
    private String state;
    private String pincode;
    private String employmentType;
    private String companyName;
    private BigDecimal monthlyIncome;
    private String profileStatus;
}
