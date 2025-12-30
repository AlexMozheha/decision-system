package com.risk.decision.dto;

import com.risk.enums.FactorClassification;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record FactorParams(

        @NotBlank(message = "Factor name cannot be empty")
        String name,

        @NotBlank(message = "Unit of measure cannot be empty")
        String unitOfMeasure,

        @NotNull(message = "Factor type cannot be null")
        FactorClassification type,

        @Size(max = 255, message = "Description max 255 characters")
        String description,

        @NotNull(message = "Factor weight cannot be null")
        @DecimalMin(value = "0.0", inclusive = false, message = "Factor weight must be positive")
        BigDecimal factorWeight,

        @NotNull(message = "Is growing flag cannot be null")
        Boolean isGrowing
) {}
