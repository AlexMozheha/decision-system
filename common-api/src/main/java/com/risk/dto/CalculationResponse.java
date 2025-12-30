package com.risk.dto;

import java.time.ZonedDateTime;
import java.util.List;

public record CalculationResponse(
        int decisionId,
        ZonedDateTime calculatedAt,
        List<AlternativeResult> results) {

    public CalculationResponse{
        if (calculatedAt == null) {
            calculatedAt = ZonedDateTime.now();}
    }
}
