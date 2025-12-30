package com.risk.calculation.controller;


import com.risk.dto.CalculationRequest;
import com.risk.dto.CalculationResponse;
import com.risk.calculation.service.CalculationCoordinator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/decisions")
@RequiredArgsConstructor
public class CalculationController {

    private final CalculationCoordinator calculationCoordinator;

    @PostMapping("/calculate")
    public CalculationResponse calculateDecisionScore(@Valid @RequestBody CalculationRequest request) {

        if (request.alternatives() == null || request.alternatives().isEmpty()) {
            throw new IllegalArgumentException("Alternatives list cannot be empty.");
        }

        return calculationCoordinator.processDecision(request);
    }
}
