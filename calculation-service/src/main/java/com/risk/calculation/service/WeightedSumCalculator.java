package com.risk.calculation.service;

import com.risk.api.dto.AltCalculationDto;
import com.risk.api.dto.EvaluationValue;
import com.risk.api.dto.FactorParams;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WeightedSumCalculator {

    public Map<Integer, BigDecimal> calculateWeightedSum(
            List<AltCalculationDto> altCalculationDtos,
            List<EvaluationValue> normalizedEvaluations,
            Map<Integer, FactorParams> factorParamsMap) {

        Map<Integer, Map<BigDecimal, BigDecimal>> scoreLookup = normalizedEvaluations.stream()
                .collect(Collectors.groupingBy(
                        EvaluationValue::factorId,
                        Collectors.toMap(EvaluationValue::rawValue, EvaluationValue::score)
                ));

        return altCalculationDtos.stream()
                .collect(Collectors.toMap(
                        AltCalculationDto::alternativeId,
                        alternative -> {
                            BigDecimal weightedSum = BigDecimal.ZERO;

                            for (EvaluationValue rawEval : alternative.values()) {
                                Integer factorId = rawEval.factorId();
                                BigDecimal rawValue = rawEval.rawValue();

                                Map<BigDecimal, BigDecimal> factorScores = Optional.ofNullable(scoreLookup.get(factorId))
                                        .orElseThrow(() -> new IllegalStateException("Normalized scores missing for factor: " + factorId));

                                BigDecimal score = Optional.ofNullable(factorScores.get(rawValue))
                                        .orElseThrow(() -> new IllegalStateException("Normalized score missing for raw value: " + rawValue));

                                BigDecimal weight = Optional.ofNullable(factorParamsMap.get(factorId))
                                        .orElseThrow(() -> new IllegalArgumentException("Factor ID " + factorId + " not found."))
                                        .factorWeight();

                                weightedSum = weightedSum.add(score.multiply(weight));
                            }
                            return weightedSum;
                        }
                ));
    }
}

