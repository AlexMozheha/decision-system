package com.risk.decision.dto;

import java.util.List;

public record DecisionDraftResponse(
        Integer id,
        String name,
        String comment,
        List<DraftAlternativeDto> alternatives
) {}
