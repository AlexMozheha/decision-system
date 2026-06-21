package com.risk.decision.dto;

import com.risk.api.validation.EvalValueForValidation;
import com.risk.api.validation.MutuallyExclusive;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

@MutuallyExclusive
public record EvaluationValue(

        @NotBlank(message = "Factor name cannot be empty")
        String factorName,

        @DecimalMin(value = "0.0", message = "Raw value must be zero or positive")
        BigDecimal rawValue,

        @DecimalMin(value = "0.0", inclusive = false, message = "Score must be positive")
        BigDecimal score)implements EvalValueForValidation {}
