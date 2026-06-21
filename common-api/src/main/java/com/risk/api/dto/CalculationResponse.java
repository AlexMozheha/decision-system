package com.risk.api.dto;

import java.time.ZonedDateTime;
import java.util.List;

public record CalculationResponse(
        int decisionId,
        ZonedDateTime calculatedAt,
        List<CalculationAlternativeResult> results) {

    public CalculationResponse{
        if (calculatedAt == null) {
            calculatedAt = ZonedDateTime.now();}
    }
}
