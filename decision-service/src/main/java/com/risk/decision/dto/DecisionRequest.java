package com.risk.decision.dto;

import com.risk.enums.CalculationMethodType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record DecisionRequest (
        @NotBlank(message = "Decision name cannot be empty")
        @Size(max = 100, message = "Decision name max 100 characters")
        String decisionName,

        @NotNull(message = "Method type cannot be null")
        CalculationMethodType method,

        @NotNull(message = "Max score cannot be null")
        @DecimalMin(value = "0.0", inclusive = false, message = "Max score must be positive")
        BigDecimal maxScore, // UInput

        @NotNull(message = "Factor parameters list cannot be null")
        @Size(min = 1, message = "At least one factor parameter is required")
        @Valid
        List<FactorParams> factorParams,

        @NotNull(message = "Alternatives list cannot be null")
        @Size(min = 1, message = "At least one alternative is required")
        @Valid
        List<Alternative> alternatives
) {}
