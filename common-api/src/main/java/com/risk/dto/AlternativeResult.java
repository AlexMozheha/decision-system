package com.risk.dto;

import java.math.BigDecimal;

public record AlternativeResult(
        int alternativeId,
        BigDecimal weightedScore,
        BigDecimal riskAdjustedScore,
        BigDecimal normalizedRisk
) {}
