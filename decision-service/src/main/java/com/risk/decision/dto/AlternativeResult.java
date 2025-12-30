package com.risk.decision.dto;

import java.math.BigDecimal;

public record AlternativeResult(
        String name,
        BigDecimal weightedScore,
        BigDecimal riskAdjustedScore,
        String riskLevel,
        boolean isRecommended
) {
}
