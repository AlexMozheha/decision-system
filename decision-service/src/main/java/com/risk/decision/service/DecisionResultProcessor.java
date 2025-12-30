package com.risk.decision.service;

import com.risk.decision.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DecisionResultProcessor {

    private final DecisionClassifierService classifierService;

    public DecisionResponse makeFinalDecision(DecisionCalculationData data) {

        BigDecimal maxWeightedRiskAdjustedScore = data.results().stream()
                .map(CalculatedAltDto::riskAdjustedScore)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal RECOMMENDATION_THRESHOLD = maxWeightedRiskAdjustedScore.multiply(new BigDecimal("0.70"));

        List<AlternativeResult> finalResults = data.results().stream()
                .map(calcAlt -> {

                    BigDecimal normRisk = calcAlt.normalizedRisk();
                    BigDecimal finalScore = calcAlt.riskAdjustedScore();

                    String riskLevel = classifierService.determineRiskLevel(normRisk);

                    boolean isRecommended = finalScore.compareTo(RECOMMENDATION_THRESHOLD) >= 0;

                    return new AlternativeResult(
                            calcAlt.name(),
                            calcAlt.weightedScore(),
                            finalScore,
                            riskLevel,
                            isRecommended
                    );
                })
                .collect(Collectors.toList());

        List<AlternativeResult> rankedAlternatives = finalResults.stream()
                .sorted(Comparator.comparing(AlternativeResult::riskAdjustedScore).reversed())
                .toList();

        return new DecisionResponse(
                data.decisionName(),
                rankedAlternatives,
                data.calculatedAt()
        );
    }
}

