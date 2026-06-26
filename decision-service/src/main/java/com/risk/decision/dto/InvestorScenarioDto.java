package com.risk.decision.dto;

import java.time.ZonedDateTime;

public record InvestorScenarioDto(
        Integer id,
        String name,
        ZonedDateTime createdAt,
        String statusName,
        ZonedDateTime calculatedAt
) {}
