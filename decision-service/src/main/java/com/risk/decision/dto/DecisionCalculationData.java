package com.risk.decision.dto;

import java.time.ZonedDateTime;
import java.util.List;

public record DecisionCalculationData(
        String decisionName,
        ZonedDateTime calculatedAt,
        List<CalculatedAltDto> results
) {
}
