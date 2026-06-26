package com.risk.decision.dto;

import java.time.ZonedDateTime;

public record AdminScenarioDto(
        Integer id,
        String name,
        String investorName,
        ZonedDateTime createdAt,
        String statusName,
        ZonedDateTime calculatedAt
) {
}
