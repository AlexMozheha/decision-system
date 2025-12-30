package com.risk.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record Alternative(
        @Min(value = 1, message = "Alternative ID must be positive")
        int alternativeId,

        @NotNull(message = "Risk coefficient cannot be null")
        @DecimalMin(value = "0.0", message = "Risk coefficient must be zero or positive")
        BigDecimal riskCoefficient,

        @NotNull(message = "Evaluation values list cannot be null")
        @Size(min = 1, message = "Alternative must have at least one evaluation value")
        @Valid
        List<EvaluationValue> values) {}
