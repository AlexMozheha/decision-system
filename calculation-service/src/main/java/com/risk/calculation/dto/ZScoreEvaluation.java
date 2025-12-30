package com.risk.calculation.dto;

import java.math.BigDecimal;

public record ZScoreEvaluation(
        int factorId,
        BigDecimal rawValue,
        BigDecimal zScore
) {
}
