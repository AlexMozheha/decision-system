package com.risk.decision.controller;


import com.risk.decision.dto.AdminScenarioDto;
import com.risk.decision.service.AdminDashboardService;
import com.risk.enums.DecisionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/scenarios")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminService;

    @GetMapping("/forAdmin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AdminScenarioDto>> getScenariosForAdmin(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) DecisionStatus status,
            Pageable pageable){
        Page<AdminScenarioDto> scenarioPage = adminService.getAllScenariosForAdmin(search, status, pageable);
        return ResponseEntity.ok(scenarioPage);
    }


}
