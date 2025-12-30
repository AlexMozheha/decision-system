package com.risk.calculation.service;


import com.risk.dto.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CalculationCoordinator {

    private final NormalizationService normalizationService;
    private final WeightedSumCalculator weightedSumCalculator;
    private final RiskAdjustmentCalculator riskAdjustmentCalculator;

    public CalculationCoordinator(
            NormalizationService normalizationService,
            WeightedSumCalculator weightedSumCalculator,
            RiskAdjustmentCalculator riskAdjustmentCalculator) {
        this.normalizationService = normalizationService;
        this.weightedSumCalculator = weightedSumCalculator;
        this.riskAdjustmentCalculator = riskAdjustmentCalculator;
    }

    public CalculationResponse processDecision(CalculationRequest request) {

        return switch (request.method()) {
            case METHOD_WS_RISK -> calculateResponseWithScores(request);
            //case METHOD_OTHER -> throw new UnsupportedOperationException("Method ID " + request.method() + " not yet supported.");
            default -> throw new IllegalArgumentException("Unknown decision method ID: " + request.method());
        };
    }


    @Transactional
    private CalculationResponse calculateResponseWithScores(CalculationRequest request) {

        final BigDecimal maxScore = request.maxScore();

        Map<Integer, FactorParams> factorParamsMap = request.factorParams().stream()
                .collect(Collectors.toMap(FactorParams::factorId, fp -> fp));


        Map<Integer, List<BigDecimal>> rawValuesByFactor = request.getRawValuesGroupedByFactor();
        Map<Integer, BigDecimal> meanValues = normalizationService.calculateMeanValues(rawValuesByFactor);
        Map<Integer, BigDecimal> stdDevValues = normalizationService.calculateStandardDeviation(rawValuesByFactor, meanValues);

        List<EvaluationValue> normalizedEvaluations = normalizationService.calculateZScoresAndScale(
                request, factorParamsMap, meanValues, stdDevValues, maxScore
        );

        Map<Integer, BigDecimal> weightedSums = weightedSumCalculator.calculateWeightedSum(
                request.alternatives(),
                normalizedEvaluations,
                factorParamsMap
        );

        //Map<Integer, BigDecimal> normalizedRisks = normalizationService.normRiskAlt(request);

        Map<Integer, BigDecimal> rawRisks = request.alternatives().stream()
                .collect(Collectors.toMap(Alternative::alternativeId, Alternative::riskCoefficient));

        // (WS * (1-R))
        Map<Integer, BigDecimal> finalScores = riskAdjustmentCalculator.calculateRiskAdjustment(
                weightedSums,
                rawRisks
        );


        List<AlternativeResult> results = request.alternatives().stream()
                .map(alt -> {
                    Integer altId = alt.alternativeId();
                    BigDecimal ws = weightedSums.getOrDefault(altId, BigDecimal.ZERO);
                    BigDecimal finalScore = finalScores.getOrDefault(altId, BigDecimal.ZERO);
                    BigDecimal normRisk = rawRisks.getOrDefault(altId, BigDecimal.ZERO);

                    return new AlternativeResult(
                            altId,
                            ws,
                            finalScore,
                            normRisk
                    );
                })
                .collect(Collectors.toList());

        return new CalculationResponse(
                request.decisionId(),
                null,
                results
        );
    }

//    private String determineRiskLevel(BigDecimal normalizedRisk) {
//        if (normalizedRisk.compareTo(new BigDecimal("0.7")) > 0) {
//            return "High";
//        } else if (normalizedRisk.compareTo(new BigDecimal("0.3")) > 0) {
//            return "Medium";
//        } else {
//            return "Low";
//        }}
}
