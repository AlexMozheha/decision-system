package com.risk.decision.dto;

import java.util.List;

public record OrderToCalculation(
        Integer id,
        String name,
        String comment,
        String investorName,
        String investorEmail,
        List<DraftAlternativeDto> alternatives
) {}
