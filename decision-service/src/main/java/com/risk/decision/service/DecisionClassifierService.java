package com.risk.decision.service;


import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DecisionClassifierService {

    public String determineRiskLevel(BigDecimal normalizedRisk) {
        final BigDecimal HIGH_THRESHOLD = new BigDecimal("0.7");
        final BigDecimal MEDIUM_THRESHOLD = new BigDecimal("0.3");

        if (normalizedRisk == null) {
            return "N/A";
        }

        if (normalizedRisk.compareTo(HIGH_THRESHOLD) > 0) {
            return "High";
        }
        else if (normalizedRisk.compareTo(MEDIUM_THRESHOLD) > 0) {
            return "Medium";
        }
        else {
            return "Low";
        }
    }
}
