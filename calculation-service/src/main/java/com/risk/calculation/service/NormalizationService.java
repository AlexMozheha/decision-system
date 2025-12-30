package com.risk.calculation.service;

import com.risk.calculation.dto.*;
import com.risk.dto.Alternative;
import com.risk.dto.CalculationRequest;
import com.risk.dto.EvaluationValue;
import com.risk.dto.FactorParams;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class NormalizationService {

    private static final int DECIMAL_SCALE = 8;
    private static final MathContext MATH_CONTEXT = new MathContext(DECIMAL_SCALE, RoundingMode.HALF_UP);




//    public Map<Integer, List<BigDecimal>> groupRawValuesByFactor(CalculationRequest request) {
//
//        return request.alternatives().stream()
//                .flatMap(alternative -> alternative.values().stream())
//                .collect(Collectors.groupingBy(
//                        EvaluationValue::factorId,
//
//                        Collectors.mapping(
//                                EvaluationValue::rawValue,
//                                Collectors.toList()
//                        )
//                ));
//    }

    public Map<Integer, BigDecimal> calculateMeanValues(Map<Integer, List<BigDecimal>> rawValuesByFactor) {

        return rawValuesByFactor.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            List<BigDecimal> rawValues = entry.getValue();
                            BigDecimal sum = rawValues.stream()
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                            BigDecimal count = BigDecimal.valueOf(rawValues.size());
                            // 8 знаків після коми й режим округлення HALF_UP
                            return sum.divide(
                                    count,
                                    MATH_CONTEXT);
                        }
                ));
    }

    public Map<Integer, BigDecimal> calculateStandardDeviation (Map<Integer, List<BigDecimal>> rawValuesByFactor,
                                                                Map<Integer, BigDecimal> meanValues) {

        return rawValuesByFactor.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey, entry -> {
                    Integer factorId = entry.getKey();
                    List<BigDecimal> rValues = entry.getValue();

                    BigDecimal mean = meanValues.get(factorId);

                    if (mean == null) {
                        throw new IllegalStateException("Mean value missing for factor ID: " + factorId);
                    }

                    BigDecimal sumOfSquaredDifferences = rValues.stream()
                            .map(v -> {
                                // (V_i - µ)
                                BigDecimal difference = v.subtract(mean);
                                // (V_i - µ)^2
                                return difference.multiply(difference);
                            })
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal count = BigDecimal.valueOf(rValues.size());
                    BigDecimal variance = sumOfSquaredDifferences.divide(
                            count,
                            MATH_CONTEXT
                    );

                    return variance.sqrt(MATH_CONTEXT);
                }));
    }


    public List<EvaluationValue> calculateZScoresAndScale(
            CalculationRequest request,
            Map<Integer, FactorParams> factorParamsMap,
            Map<Integer, BigDecimal> meanValues,
            Map<Integer, BigDecimal> stdDevValues,
            BigDecimal maxScore) {

        List<ZScoreEvaluation> allEvaluationsWithZScore = request.alternatives().stream()
                .flatMap(alternative -> alternative.values().stream())
                .map(eval -> {
                    int factorId = eval.factorId();
                    BigDecimal rawValue = eval.rawValue();

                    BigDecimal mean = meanValues.get(factorId);
                    BigDecimal stdDev = stdDevValues.get(factorId);
                    FactorParams params = factorParamsMap.get(factorId);

                    BigDecimal zScore;
                    if (stdDev == null || stdDev.compareTo(BigDecimal.ZERO) == 0) {
                        zScore = BigDecimal.ZERO;
                    } else {
                        zScore = (rawValue.subtract(mean)).divide(stdDev, MATH_CONTEXT);
                    }

                    if (!params.isGrowing()) {
                        zScore = zScore.multiply(new BigDecimal("-1"));
                    }

                    return new ZScoreEvaluation(
                            factorId,
                            rawValue,
                            zScore
                    );
                }).collect(Collectors.toList());

        BigDecimal zMin = allEvaluationsWithZScore.stream()
                .map(ZScoreEvaluation::zScore)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal zMax = allEvaluationsWithZScore.stream()
                .map(ZScoreEvaluation::zScore)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal zRange = zMax.subtract(zMin);

        if (zRange.compareTo(BigDecimal.ZERO) == 0) {
            BigDecimal defaultScore = maxScore.divide(new BigDecimal("2"), MATH_CONTEXT);
            return allEvaluationsWithZScore.stream()
                    .map(eval -> new EvaluationValue(eval.factorId(), eval.rawValue(), defaultScore))
                    .collect(Collectors.toList());
        }

        return allEvaluationsWithZScore.stream()
                .map(eval -> {
                    BigDecimal currentZ = eval.zScore();

                    // Score = maxScore * (Z - Z_min) / (Z_max - Z_min)
                    BigDecimal numerator = currentZ.subtract(zMin);
                    BigDecimal normalizedValue = numerator.divide(zRange, MATH_CONTEXT);

                    BigDecimal finalScore = maxScore.multiply(normalizedValue);

                    return new EvaluationValue(
                            eval.factorId(),
                            eval.rawValue(),
                            finalScore.setScale(DECIMAL_SCALE, RoundingMode.HALF_UP)
                    );
                })
                .collect(Collectors.toList());
    }

    public Map<Integer, BigDecimal> normRiskAlt(CalculationRequest request) {

        Map<Integer, BigDecimal> allRisks = request.alternatives().stream().collect(Collectors.toMap(Alternative::alternativeId, Alternative::riskCoefficient));

        BigDecimal maxRisk = allRisks.values().stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        BigDecimal minRisk = allRisks.values().stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);

        BigDecimal riskRange = maxRisk.subtract(minRisk);

        if (riskRange.compareTo(BigDecimal.ZERO) == 0) {
            return allRisks.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> BigDecimal.ZERO));
        }

        // [0.1,0.9] (0.9-0,1 = 0.8 = RANGE)
        final BigDecimal NEW_MIN = new BigDecimal("0.1");
        final BigDecimal NEW_MAX = new BigDecimal("0.75");
        final BigDecimal NEW_RANGE = NEW_MAX.subtract(NEW_MIN);

        final BigDecimal ABS_MAX = new BigDecimal("0.70");
        final BigDecimal ABS_RANGE = new BigDecimal("1.0");

        return  allRisks.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            BigDecimal rawRisk = entry.getValue();


                            // (Risk - Min) / Range
                             BigDecimal oldRisk = (rawRisk.subtract(minRisk)).divide(riskRange, MATH_CONTEXT);

                             // New_Min + Old_Normalized_Value * New_Range
                             BigDecimal relativeNorm = NEW_MIN.add(oldRisk.multiply(NEW_RANGE));

                            BigDecimal absoluteNorm = rawRisk.min(ABS_MAX);

                            return relativeNorm.min(absoluteNorm);
                        }
                ));

    }



}
