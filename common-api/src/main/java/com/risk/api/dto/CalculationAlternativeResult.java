package com.risk.api.dto;

import java.math.BigDecimal;
import java.util.Map;

public record CalculationAlternativeResult(
        int alternativeId,
        BigDecimal weightedScore,
        BigDecimal riskAdjustedScore,
        BigDecimal normalizedRisk,
        Map<Integer, BigDecimal> factorScores
) {}
