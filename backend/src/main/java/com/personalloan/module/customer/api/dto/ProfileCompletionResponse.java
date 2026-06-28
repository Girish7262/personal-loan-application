package com.personalloan.module.customer.api.dto;

import java.util.List;

public record ProfileCompletionResponse(
    int percentage,
    ProfileStatus status,
    List<String> missingFields
) {}
