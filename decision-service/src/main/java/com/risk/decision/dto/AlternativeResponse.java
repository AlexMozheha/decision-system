package com.risk.decision.dto;

import java.math.BigDecimal;
import java.util.Map;

public record AlternativeResponse(
        String name,
        BigDecimal weightedScore,
        BigDecimal riskAdjustedScore,
        String riskLevel,
        boolean isRecommended,
        Map<String,BigDecimal> factorScores
) {
}
