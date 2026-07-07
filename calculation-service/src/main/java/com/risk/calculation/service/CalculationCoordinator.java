package com.risk.calculation.service;


import com.risk.api.dto.*;
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


    private CalculationResponse calculateResponseWithScores(CalculationRequest request) {

        final BigDecimal maxScore = request.maxScore();

        Map<Integer, FactorParams> factorParamsMap = request.factorParams().stream()
                .collect(Collectors.toMap(FactorParams::factorId, fp -> fp));


        Map<Integer, List<BigDecimal>> rawValuesByFactor = request.altCalculationDtos().stream()
                    .flatMap(alternative -> alternative.values().stream())
                    .filter(eval -> eval.rawValue() != null)
                    .collect(Collectors.groupingBy(
                            EvaluationValue::factorId,
                            Collectors.mapping(EvaluationValue::rawValue, Collectors.toList())
                    ));


        Map<Integer, BigDecimal> meanValues = normalizationService.calculateMeanValues(rawValuesByFactor);
        Map<Integer, BigDecimal> stdDevValues = normalizationService.calculateStandardDeviation(rawValuesByFactor, meanValues);

        List<EvaluationValue> normalizedEvaluations = normalizationService.calculateZScoresAndScale(
                request, factorParamsMap, meanValues, stdDevValues, maxScore
        );

        Map<Integer, BigDecimal> weightedSums = weightedSumCalculator.calculateWeightedSum(
                request.altCalculationDtos(),
                normalizedEvaluations,
                factorParamsMap
        );

        Map<Integer, BigDecimal> rawRisks = request.altCalculationDtos().stream()
                .collect(Collectors.toMap(AltCalculationDto::alternativeId, AltCalculationDto::riskCoefficient));

        // (WS * (1-R))
        Map<Integer, BigDecimal> finalScores = riskAdjustmentCalculator.calculateRiskAdjustment(
                weightedSums,
                rawRisks
        );


                List<CalculationAlternativeResult> results = request.altCalculationDtos().stream()
                .map(alt -> {
                    Integer altId = alt.alternativeId();
                    BigDecimal ws = weightedSums.getOrDefault(altId, BigDecimal.ZERO);
                    BigDecimal finalScore = finalScores.getOrDefault(altId, BigDecimal.ZERO);
                    BigDecimal normRisk = rawRisks.getOrDefault(altId, BigDecimal.ZERO);

                    Map<Integer, BigDecimal> factorScores = alt.values().stream()
                            .collect(Collectors.toMap(
                                    EvaluationValue::factorId,
                                    eval -> {
                                        if (eval.rawValue() == null) {
                                            return eval.score();
                                        }

                                        return normalizedEvaluations.stream()
                                                .filter(normEval -> normEval.factorId() == eval.factorId()
                                                        && normEval.rawValue().compareTo(eval.rawValue()) == 0)
                                                .map(EvaluationValue::score)
                                                .findFirst()
                                                .orElse(BigDecimal.ZERO);
                                    },
                                    (existing, replacement) -> existing
                            ));

                    return new CalculationAlternativeResult(
                            altId,
                            ws,
                            finalScore,
                            normRisk,
                            factorScores
                    );
                })
                .collect(Collectors.toList());

                return new CalculationResponse(
                request.decisionId(),
                null,
                results
        );
    }
}
