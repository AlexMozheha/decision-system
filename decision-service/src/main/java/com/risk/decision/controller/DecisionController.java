package com.risk.decision.controller;


import com.risk.decision.dto.DecisionRequest;
import com.risk.decision.dto.DecisionResponse;
import com.risk.decision.service.DecisionOrchestratorService;
import com.risk.decision.service.DecisionResultProcessor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/decisions")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class DecisionController {

    private final DecisionOrchestratorService orchestratorService;

@PostMapping("/make-decision")
    public ResponseEntity<DecisionResponse> makeDecision(@Valid @RequestBody DecisionRequest request) {

        if (request.alternatives() == null || request.alternatives().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        DecisionResponse response = orchestratorService.makeDecision(request);
        return ResponseEntity.ok(response);
    }
}
