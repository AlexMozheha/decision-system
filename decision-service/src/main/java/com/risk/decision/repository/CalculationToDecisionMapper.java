package com.risk.decision.repository;

import com.risk.api.dto.CalculationAlternativeResult;
import com.risk.api.dto.CalculationResponse;
import com.risk.decision.dto.CalculatedAltDto;
import com.risk.decision.dto.DecisionCalculationData;
import com.risk.decision.service.AlternativeService;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface CalculationToDecisionMapper {

    
    default DecisionCalculationData toDecisionCalculationData(
            CalculationResponse response,
            String decisionName,
            AlternativeService alternativeService
    ) {

        List<Integer> alternativeIds = response.results().stream()
                .map(CalculationAlternativeResult::alternativeId)
                .toList();

        Map<Integer, String> alternativeNames = alternativeService.preloadAlternativeNames(alternativeIds);

        List<CalculatedAltDto> mapped = response.results().stream()
                .map(calc -> new CalculatedAltDto(
                        alternativeNames.get(calc.alternativeId()),
                        calc.weightedScore(),
                        calc.riskAdjustedScore(),
                        calc.normalizedRisk()
                ))
                .toList();

        return new DecisionCalculationData(
                decisionName,
                response.calculatedAt(),
                mapped
        );
    }
}
