package com.risk.decision.controller;


import com.risk.decision.dto.CreateDecisionRequest;
import com.risk.decision.dto.DecisionDraftResponse;
import com.risk.decision.dto.InvestorScenarioDto;
import com.risk.decision.service.CreationDecisionService;
import com.risk.decision.service.InvestorDashboardService;
import com.risk.decision.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scenarios")
@RequiredArgsConstructor
public class InvestorScenarioController {

    private final CreationDecisionService creationDecisionService;
    private final InvestorDashboardService investorService;
    private final JwtService jwtService;

    @PreAuthorize("hasRole('INVESTOR')")
    @GetMapping
    public ResponseEntity<Page<InvestorScenarioDto>> getMyScenarios(
            @RequestParam(required = false) String search, @RequestAttribute("userId") Integer userId,
            Pageable pageable) {

        Page<InvestorScenarioDto> scenariosPage = investorService
                .getInvestorScenarios(userId, search, pageable);

        return ResponseEntity.ok(scenariosPage);
    }

    @PreAuthorize("hasRole('INVESTOR')")
    @PostMapping
    public ResponseEntity<Integer> createDecision(
            @Valid @RequestBody CreateDecisionRequest createDecisionRequest,
            @RequestAttribute("userId") Integer userId) {

        return ResponseEntity.ok(creationDecisionService.saveDecision(createDecisionRequest, userId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'INVESTOR')")
    @GetMapping("/{id}/draft")
    public ResponseEntity<DecisionDraftResponse> getDraftForEditing(
            @PathVariable("id") Integer id,
            @RequestHeader("Authorization") String authHeader,
            Authentication authentication) {

        String token = authHeader.substring(7);

        Integer currentUserId = jwtService.extractUserId(token);

        return ResponseEntity.ok(creationDecisionService.getDecisionDraft(id, currentUserId));
    }

    @PreAuthorize("hasRole('INVESTOR')")
    @GetMapping("/{id}")
    public ResponseEntity<InvestorScenarioDto> getScenarioById(
            @PathVariable Integer id,
            @RequestAttribute("userId") Integer userId) {

        return ResponseEntity.ok(investorService.getDecisionByIdAndUserId(id, userId));
    }


}
