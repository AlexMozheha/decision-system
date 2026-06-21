package com.risk.api.dto;

import java.math.BigDecimal;

public record CalculationAlternativeResult(
        int alternativeId,
        BigDecimal weightedScore,
        BigDecimal riskAdjustedScore,
        BigDecimal normalizedRisk
) {}
