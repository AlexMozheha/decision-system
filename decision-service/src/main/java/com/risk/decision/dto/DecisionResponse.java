package com.risk.decision.dto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

public record DecisionResponse(
        String decisionName,
        List<AlternativeResult> alternatives,
        ZonedDateTime calculatedAt
) {}
