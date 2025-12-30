package com.risk.decision.repository;

import com.risk.dto.AlternativeResult;
import com.risk.dto.CalculationResponse;
import com.risk.decision.dto.CalculatedAltDto;
import com.risk.decision.dto.DecisionCalculationData;
import com.risk.decision.model.Alternative;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class CalculationToDecisionMapper {

    @Autowired
    private AlternativeRepository alternativeRepository;

    public DecisionCalculationData toDecisionCalculationData(
            CalculationResponse response,
            String decisionName
    ) {
        List<CalculatedAltDto> mapped = response.results().stream()
                .map(this::mapAlternative)
                .toList();

        return new DecisionCalculationData(
                decisionName,
                response.calculatedAt(),
                mapped
        );
    }

    private CalculatedAltDto mapAlternative(AlternativeResult calc) {
        Alternative alt = alternativeRepository
                .findById(calc.alternativeId())
                .orElseThrow();

        return new CalculatedAltDto(
                alt.getName(),
                calc.weightedScore(),
                calc.riskAdjustedScore(),
                calc.normalizedRisk()
        );
    }
}
