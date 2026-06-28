package com.personalloan.module.auth.api.dto;

public record UserSummary(
    Long id,
    String email,
    String role,
    boolean active
) {}
