package com.risk.decision.dto;

import java.math.BigDecimal;

public record CalculatedAltDto(
        String name,
        BigDecimal weightedScore,
        BigDecimal riskAdjustedScore,
        BigDecimal normalizedRisk
) {
}
