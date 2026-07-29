package com.risk.decision.service;

import com.risk.decision.dto.InvestorScenarioDto;
import com.risk.decision.exeption.ResourceNotFoundException;
import com.risk.decision.model.Decision;
import com.risk.decision.repository.DecisionMapper;
import com.risk.decision.repository.DecisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class InvestorDashboardService {

    private final DecisionRepository decisionRepository;
    private final DecisionMapper decisionMapper;

    @Transactional(readOnly = true)
    public Page<InvestorScenarioDto> getInvestorScenarios (Integer userId, String search, Pageable pageable) {

        String searchParam = (search == null) ? "" : search.strip();

        Page<Decision> decisionPage = decisionRepository.findMyScenarios(userId, searchParam, pageable);

        return decisionPage.map(decision -> {

            ZonedDateTime calculatedAt = null;

            var currentStatus = decision.getStatus().getName();

            if (currentStatus == com.risk.enums.DecisionStatus.CALCULATED
                    && decision.getCalculationResults() != null
                    && !decision.getCalculationResults().isEmpty()) {

                calculatedAt = decision.getCalculationResults().getFirst().getCalculatedAt();}

            return new InvestorScenarioDto(
                    decision.getId(),
                    decision.getName(),
                    decision.getCreatedAt(),
                    decision.getStatus().getName().name(),
                    calculatedAt
            );
        });
    }

    public InvestorScenarioDto getDecisionByIdAndUserId(Integer id, Integer userId) {
        Decision decision = decisionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Scenario not found"));

        if (!decision.getUserId().equals(userId)) {
            throw new AccessDeniedException("You don't have access to this scenario");
        }

        return decisionMapper.toInvestorScenarioDto(decision);
    }
}
