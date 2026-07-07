package com.risk.decision.service;

import com.risk.api.dto.CalculationAlternativeResult;
import com.risk.decision.model.Alternative;
import com.risk.api.dto.CalculationResponse;
import com.risk.decision.model.CalculationResult;
import com.risk.decision.model.Decision;
import com.risk.decision.repository.AlternativeRepository;
import com.risk.decision.repository.CalculationResultRepository;
import com.risk.decision.repository.DecisionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalculationResultPersistenceService {

    private final DecisionRepository decisionRepository;
    private final AlternativeRepository alternativeRepository;
    private final CalculationResultRepository calculationResultRepository;
    private final DecisionClassifierService classifierService;

    @Transactional
    public void persistResults(Decision decision, CalculationResponse response) {

        List<Integer> alternativeIds = response.results().stream()
                .map(CalculationAlternativeResult::alternativeId)
                .toList();

        Map<Integer, Alternative> alternativeMap = decision.getAlternatives().stream()
                .collect(Collectors.toMap(Alternative::getId, alt -> alt));

        if (alternativeMap.size() != alternativeIds.size()) {
            throw new IllegalStateException("One or more Alternative IDs in the calculation response were not found in the database.");
        }

        ZonedDateTime calculatedAt = response.calculatedAt() != null ? response.calculatedAt() : ZonedDateTime.now();
        List<CalculationResult> resultsToSave = new ArrayList<>();

        if (decision.getCalculationResults() == null) {
            decision.setCalculationResults(new ArrayList<>());
        }

        for (CalculationAlternativeResult altResult : response.results()) {

            Alternative alternative = alternativeMap.get(altResult.alternativeId());
            String classifiedRiskLevel = classifierService.determineRiskLevel(altResult.normalizedRisk());

            CalculationResult entity = CalculationResult.builder()
                    .decision(decision)
                    .alternative(alternative)
                    .weightedScore(altResult.weightedScore())
                    .riskAdjustedScore(altResult.riskAdjustedScore())
                    .riskLevel(classifiedRiskLevel)
                    .calculatedAt(calculatedAt)
                    .build();

            resultsToSave.add(entity);

            if (alternative.getCalculationResults() == null) {
                alternative.setCalculationResults(new ArrayList<>());
            }
            alternative.getCalculationResults().add(entity);
            decision.getCalculationResults().add(entity);
        }
        calculationResultRepository.saveAll(resultsToSave);
    }
}
