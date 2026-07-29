package com.risk.decision.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateDecisionRequest(
        Integer id,

        @NotBlank(message = "Scenario name cannot be empty")
        @Size(max = 150, message = "Scenario name max 150 characters")
        String name,

        String comment,

        @NotNull(message = "Alternatives list cannot be null")
        @Size(min = 1, message = "At least one alternative is required")
        @Valid
        List<CreateAlternativeDto> alternatives
) {}
