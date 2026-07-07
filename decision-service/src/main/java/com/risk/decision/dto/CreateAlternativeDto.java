package com.risk.decision.dto;

import jakarta.validation.constraints.*;

public record CreateAlternativeDto(
        @NotBlank(message = "Alternative name cannot be empty")
        @Size(max = 100, message = "Alternative name max 100 characters")
        String name,

        @NotNull(message = "Risk value cannot be null")
        @Min(value = 0, message = "Risk value cannot be less than 0%")
        @Max(value = 100, message = "Risk value cannot exceed 100%")
        Integer riskCoefficient
) {}
