package com.risk.decision.dto;

import com.risk.enums.DecisionStatus;

public record DecisionCreatedResponse(
        Integer id,
        DecisionStatus status
) {}
