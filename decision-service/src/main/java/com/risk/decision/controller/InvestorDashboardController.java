package com.risk.decision.controller;


import com.risk.decision.dto.InvestorScenarioDto;
import com.risk.decision.service.InvestorDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scenarios")
@RequiredArgsConstructor
public class InvestorDashboardController {

    private final InvestorDashboardService investorService;


    @GetMapping("/my")
    @PreAuthorize("hasRole('INVESTOR')")
    public ResponseEntity<Page<InvestorScenarioDto>> getMyScenarios(
            @RequestParam(required = false) String search, @RequestAttribute("userId") Integer userId,
            Pageable pageable) {

        Page<InvestorScenarioDto> scenariosPage = investorService
                .getInvestorScenarios(userId, search, pageable);

        return ResponseEntity.ok(scenariosPage);
    }

}
