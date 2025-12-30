package com.risk.dto;

import com.risk.validation.EvalValueForValidation;
import com.risk.validation.MutuallyExclusive;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

// Унікальна оцінка для альтернативи за фактором.
@MutuallyExclusive
public record EvaluationValue (
        @Min(value = 1, message = "Factor ID must be positive")
        int factorId,

        @DecimalMin(value = "0.0", message = "Raw value must be zero or positive")
        BigDecimal rawValue,

        @DecimalMin(value = "0.0", inclusive = false, message = "Score must be positive")
        BigDecimal score)implements EvalValueForValidation{}
