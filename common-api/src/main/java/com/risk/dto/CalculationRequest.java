package com.risk.dto;

import com.risk.enums.CalculationMethodType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record CalculationRequest(
        @Min(value = 1, message = "Decision ID must be positive")
        int decisionId,

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
) {

    public Map<Integer, List<BigDecimal>> getRawValuesGroupedByFactor() {

        return this.alternatives().stream()
                .flatMap(alternative -> alternative.values().stream())

                .collect(Collectors.groupingBy(
                        EvaluationValue::factorId,
                        Collectors.mapping(EvaluationValue::rawValue, Collectors.toList())
                ));
    }
}
