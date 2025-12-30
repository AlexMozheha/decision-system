package com.risk.decision.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record Alternative(

        @NotBlank(message = "Alternative name cannot be empty")
        String name,

        @Size(max = 255, message = "Description max 255 characters")
        String description,

        @NotNull(message = "Risk coefficient cannot be null")
        @DecimalMin(value = "0.0", message = "Risk coefficient must be zero or positive")
        BigDecimal riskCoefficient,

        @NotNull(message = "Evaluation values list cannot be null")
        @Valid
        List<EvaluationValue> values
) {}
