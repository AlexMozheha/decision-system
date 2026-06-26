package com.risk.decision.service;

import com.risk.decision.dto.InvestorScenarioDto;
import com.risk.decision.model.Decision;
import com.risk.decision.repository.DecisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class InvestorDashboardService {

    private final DecisionRepository decisionRepository;

    public Page<InvestorScenarioDto> getInvestorScenarios (Integer userId, String search, Pageable pageable) {

        String searchParam = (search == null) ? "" : search.trim();

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
}
