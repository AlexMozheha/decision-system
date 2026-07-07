package com.risk.decision.dto;

public record DraftAlternativeDto(
        Integer id,
        String name,
        Integer riskCoefficient
) {}
