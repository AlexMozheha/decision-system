package com.risk.decision.service;


import com.risk.api.dto.CalculationAlternativeResult;
import com.risk.decision.client.CalculationServiceClient;
import com.risk.decision.dto.*;
import com.risk.decision.model.*;
import com.risk.decision.repository.*;
import com.risk.api.dto.CalculationRequest;
import com.risk.api.dto.CalculationResponse;
import com.risk.enums.CalculationMethodType;
import com.risk.enums.DecisionStatus;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DecisionOrchestratorService {

    private final DecisionValidationService validationService;

    private final DecisionRepository decisionRepository;
    private final CalculationMethodRepository calculationMethodRepository;

    private final DecisionMapper decisionMapper;
    private final CalculationRequestMapper calculationRequestMapper;

    private final CalculationServiceClient calculationClient;
    private final CalculationResultPersistenceService persistenceService;

    private final StatusRepository statusRepository;



    @Transactional
    public DecisionCreatedResponse makeDecision(DecisionRequest request) {

        validationService.validateFactorDataConsistency(request);

        CalculationMethod methodEntity = validationService.validateMethodByName(calculationMethodRepository, request);


        Decision existingDecision = decisionRepository.findById(request.decisionId())
                .orElseThrow(() -> new EntityNotFoundException("Decision with ID " + request.decisionId() + " not found"));

        decisionMapper.updateEntityFromRequest(request, methodEntity, existingDecision);

        existingDecision.getFactors().clear();
        existingDecision.getAlternatives().clear();
        decisionRepository.saveAndFlush(existingDecision);

        if (request.factorParams() != null) {
            for (FactorParams fp : request.factorParams()) {
                Factor f = decisionMapper.toFactor(fp);
                f.setDecision(existingDecision);
                existingDecision.getFactors().add(f);
            }
        }

        if (request.alternativeRequests() != null) {
            for (AlternativeRequest altDto : request.alternativeRequests()) {
                Alternative altEntity = decisionMapper.toAlternative(altDto);
                altEntity.setDecision(existingDecision);

                if (altDto.riskCoefficient() != null) {
                    BigDecimal riskPercent = altDto.riskCoefficient()
                            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                    altEntity.setRiskCoefficient(riskPercent);
                } else {
                    altEntity.setRiskCoefficient(BigDecimal.ZERO);
                }

                altEntity.setEvaluations(new ArrayList<>());
                existingDecision.getAlternatives().add(altEntity);
            }
        }

        existingDecision = decisionRepository.saveAndFlush(existingDecision);

        Map<String, Factor> factorByName = existingDecision.getFactors()
                .stream()
                .collect(Collectors.toMap(
                        f -> f.getName().toLowerCase(),
                        f -> f,
                        (existing, replacement) -> existing
                ));

        Map<String, Alternative> alternativeByName = existingDecision.getAlternatives()
                .stream()
                .collect(Collectors.toMap(
                        a -> a.getName().toLowerCase(),
                        a -> a,
                        (existing, replacement) -> existing
                ));

        if (request.alternativeRequests() != null) {
            for (AlternativeRequest altDto : request.alternativeRequests()) {
                Alternative savedAlt = alternativeByName.get(altDto.name().toLowerCase());

                if (savedAlt != null && altDto.values() != null) {
                    if (savedAlt.getEvaluations() == null) {
                        savedAlt.setEvaluations(new ArrayList<>());
                    } else {
                        savedAlt.getEvaluations().clear();
                    }

                    for (EvaluationValue evalDto : altDto.values()) {
                        Factor matchedFactor = factorByName.get(evalDto.factorName().toLowerCase());
                        if (matchedFactor == null) {
                            throw new IllegalStateException("Factor '" + evalDto.factorName() + "' not found for evaluation");
                        }

                        Evaluation evalEntity = Evaluation.builder()
                                .alternative(savedAlt)
                                .factor(matchedFactor)
                                .rawValue(evalDto.rawValue())
                                .score(evalDto.score())
                                .build();

                        savedAlt.getEvaluations().add(evalEntity);
                    }
                }
            }
        }

        Decision savedDecision = decisionRepository.saveAndFlush(existingDecision);

        CalculationMethodType methodType = methodEntity.getName();
        CalculationRequest calcRequest = calculationRequestMapper.toCalculationRequest(savedDecision, methodType);

        log.info("Sending CalculationRequest to calculation-service: {}", calcRequest);

        CalculationResponse calcResponse = calculationClient.calculate(calcRequest);

        persistenceService.persistResults(savedDecision, calcResponse);

        Map<Integer, Map<Integer, BigDecimal>> calcResultsMap = calcResponse.results().stream()
                .collect(Collectors.toMap(
                        res -> res.alternativeId(),
                        res -> res.factorScores() != null ? res.factorScores() : Collections.emptyMap()
                ));

        if (savedDecision.getAlternatives() != null) {
            for (Alternative alt : savedDecision.getAlternatives()) {

                if (calcResultsMap.containsKey(alt.getId())) {
                    Map<Integer, BigDecimal> scoresForFactors = calcResultsMap.get(alt.getId());

                    if (alt.getEvaluations() != null) {
                        for (Evaluation eval : alt.getEvaluations()) {
                            int currentFactorId = eval.getFactor().getId();

                            if (scoresForFactors.containsKey(currentFactorId)) {
                                eval.setScore(scoresForFactors.get(currentFactorId));
                            }
                        }
                    }
                }
            }
        }

        Status calculatedStatus = statusRepository.findByName(DecisionStatus.CALCULATED)
                .orElseThrow(() -> new RuntimeException("Status CALCULATED not found in database"));

        savedDecision.setStatus(calculatedStatus);
        Decision finalSaved = decisionRepository.save(savedDecision);

        return new DecisionCreatedResponse(finalSaved.getId(), finalSaved.getStatus().getName());
    }

}
