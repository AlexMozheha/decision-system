package com.risk.decision.service;

import com.risk.decision.dto.AlternativeResponse;
import com.risk.decision.dto.CalculatedAltDto;
import com.risk.decision.dto.DecisionCalculationData;
import com.risk.decision.dto.DecisionResponse;
import com.risk.decision.model.Alternative;
import com.risk.decision.model.CalculationResult;
import com.risk.decision.model.Decision;
import com.risk.decision.repository.DecisionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DecisionResultProcessor {

    private final DecisionRepository decisionRepository;

    private static final BigDecimal THRESHOLD_COEFFICIENT = new BigDecimal("0.70");

    public List<String> generateAnalyticalSummaryList(List<AlternativeResponse> rankedAlts, boolean isUk) {

        List<String> recommendations = new ArrayList<>();

        if (rankedAlts == null || rankedAlts.isEmpty()) {
            recommendations.add(isUk ? "Немає даних для формування рекомендацій." : "No data is available to formulate recommendations.");
            return recommendations;
        }

        AlternativeResponse leader = rankedAlts.get(0);

        if (isUk) {
            recommendations.add(String.format(
                    "**%s** отримала найвищий рейтинг (%s), що свідчить про її найбільшу ефективність. Дана альтернатива рекомендується до вибору.",
                    leader.name(), leader.riskAdjustedScore()
            ));
        } else {
            recommendations.add(String.format(
                    "**%s** received the highest rating (%s), which indicates that it is the most effective. This alternative is recommended.",
                    leader.name(), leader.riskAdjustedScore()
            ));
        }

        for (int i = 1; i < rankedAlts.size(); i++) {
            AlternativeResponse alt = rankedAlts.get(i);

            if (alt.isRecommended()) {
                if (isUk) {
                    recommendations.add(String.format(
                            "**%s** (%s) має нижчий результат, проте перевищує порогове значення, тому також може бути рекомендована до розгляду.",
                            alt.name(), alt.riskAdjustedScore()
                    ));
                } else {
                    recommendations.add(String.format(
                            "**%s** (%s) has a lower score but exceeds the threshold, so it may also be recommended for consideration.",
                            alt.name(), alt.riskAdjustedScore()
                    ));
                }
            } else {
                if (isUk) {
                    recommendations.add(String.format(
                            "**%s** (%s) не досягла порогового значення, тому не рекомендується до вибору.",
                            alt.name(), alt.riskAdjustedScore()
                    ));
                } else {
                    recommendations.add(String.format(
                            "**%s** (%s) did not meet the threshold, so it is not recommended for selection.",
                            alt.name(), alt.riskAdjustedScore()
                    ));
                }
            }
        }
        return recommendations;
    }

    @Transactional(readOnly = true)
    public DecisionResponse makeResultToCharts(Integer decisionId, boolean isUk) {

        Optional<Decision> decisionOpt = decisionRepository.findResultById(decisionId);

        if (decisionOpt.isEmpty()) {
            throw new EntityNotFoundException("Decision with ID " + decisionId + " not found.");
        }

        Decision decision = decisionOpt.get();

        BigDecimal maxWeightedRiskAdjustedScore = decision.getCalculationResults().stream()
                .map(CalculationResult::getRiskAdjustedScore).max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);

        BigDecimal recommendationThreshold = maxWeightedRiskAdjustedScore.multiply(THRESHOLD_COEFFICIENT);

        Map<Integer, CalculationResult> resultByAltId = decision.getCalculationResults().stream()
                .collect(Collectors.toMap(
                        res -> res.getAlternative().getId(),
                        res -> res,
                        (existing, replacement) -> existing
                ));

        List<AlternativeResponse> alternativeResponses = decision.getAlternatives().stream()
                .map(alt -> {
                    CalculationResult result = resultByAltId.get(alt.getId());

                    BigDecimal weightedScore = (result != null) ? result.getWeightedScore() : BigDecimal.ZERO;
                    BigDecimal riskAdjustedScore = (result != null) ? result.getRiskAdjustedScore() : BigDecimal.ZERO;

                    String riskLevel = (result != null) ? result.getRiskLevel() : "UNKNOWN";

                    boolean isRecommended = riskAdjustedScore.compareTo(recommendationThreshold) >= 0;

                    Map<String, BigDecimal> factorScores = alt.getEvaluations() != null ? alt.getEvaluations().stream()
                            .collect(Collectors.toMap(
                                    ev -> ev.getFactor().getName(),
                                    ev -> ev.getScore() != null ? ev.getScore() : BigDecimal.ZERO,
                                    (existing, replacement) -> existing
                            ))
                            : Collections.emptyMap();

                    return new AlternativeResponse(
                            alt.getName(),
                            weightedScore,
                            riskAdjustedScore,
                            riskLevel,
                            isRecommended,
                            factorScores
                    );}
                ).toList();

        List<AlternativeResponse> rankedAlternatives = alternativeResponses.stream()
                .sorted(Comparator.comparing(AlternativeResponse::riskAdjustedScore).reversed())
                .toList();

        List<String> recommendations = generateAnalyticalSummaryList(rankedAlternatives, isUk);

        return new DecisionResponse(
                decision.getName(),
                rankedAlternatives,
                decision.getCreatedAt(),
                recommendations
        );
    }
}

