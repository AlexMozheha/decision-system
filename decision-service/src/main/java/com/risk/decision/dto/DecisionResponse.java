package com.risk.decision.dto;

import java.time.ZonedDateTime;
import java.util.List;

public record DecisionResponse(
        String decisionName,
        List<AlternativeResponse> alternatives,
        ZonedDateTime calculatedAt,
        List<String> recommendation
) {}
