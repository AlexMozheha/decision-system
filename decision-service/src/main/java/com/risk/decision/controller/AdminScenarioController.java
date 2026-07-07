package com.risk.decision.controller;


import com.risk.decision.dto.*;
import com.risk.decision.service.AdminDashboardService;
import com.risk.decision.service.DecisionOrchestratorService;
import com.risk.enums.DecisionStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class AdminScenarioController {

    private final DecisionOrchestratorService orchestratorService;
    private final AdminDashboardService adminService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/scenarios")
    public ResponseEntity<Page<AdminScenarioDto>> getScenariosForAdmin(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) DecisionStatus status,
            Pageable pageable){
        Page<AdminScenarioDto> scenarioPage = adminService.getAllScenariosForAdmin(search, status, pageable);
        return ResponseEntity.ok(scenarioPage);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/scenarios/calculations")
    public ResponseEntity<DecisionCreatedResponse> makeDecision(@Valid @RequestBody DecisionRequest request) {

        if (request.alternativeRequests() == null || request.alternativeRequests().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        DecisionCreatedResponse response = orchestratorService.makeDecision(request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/scenarios/{id}/lock-for-calculation")
    public ResponseEntity<Void> lockScenarioForAdmin(@PathVariable("id") Integer id) {
        adminService.lockScenarioForCalculation(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/scenarios/order")
    public ResponseEntity<OrderToCalculation> getOrderToCalculation(@RequestParam("decisionId") Integer decisionId) {

        return ResponseEntity.ok(adminService.getDecisionOrder(decisionId));
    }



}
