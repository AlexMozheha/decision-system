package com.risk.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

// Це властивість ФАКТОРА, яке є спільним для всіх альтернатив. (Єдине джерело правди).
public record FactorParams(
        @Min(value = 1, message = "Factor ID must be positive")
        int factorId,

        @NotNull(message = "Factor weight cannot be null")
        @DecimalMin(value = "0.0", inclusive = false, message = "Factor weight must be positive")
        BigDecimal factorWeight,

        @NotNull(message = "Is growing flag cannot be null")
        Boolean isGrowing // Тип впливу: true = більше краще
) {}
