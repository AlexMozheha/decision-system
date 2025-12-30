package com.risk.calculation.service;


import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RiskAdjustmentCalculator {

    public Map<Integer, BigDecimal> calculateRiskAdjustment(Map<Integer, BigDecimal> weightedSumsByAlternative, Map<Integer, BigDecimal> normalizedRisks) {

        return normalizedRisks.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        ent -> {
                            Integer altId = ent.getKey();
                            BigDecimal normalizedRisk = ent.getValue();
                            BigDecimal ws = Optional.ofNullable(weightedSumsByAlternative.get(altId))
                                    .orElseThrow(() -> new IllegalArgumentException("Weighted sum missing for Alternative ID:" + altId));;

                            // WS * (1 - R)
                            BigDecimal riskFactor = new BigDecimal("1").subtract(normalizedRisk);
                            return ws.multiply(riskFactor);
                        }
                ));
    }

}
