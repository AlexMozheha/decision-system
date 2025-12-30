package com.risk.decision.service;


import com.risk.client.CalculationServiceClient;
import com.risk.dto.CalculationRequest;
import com.risk.dto.CalculationResponse;
import com.risk.decision.dto.DecisionCalculationData;
import com.risk.decision.dto.DecisionRequest;
import com.risk.decision.dto.DecisionResponse;
import com.risk.decision.dto.EvaluationValue;
import com.risk.decision.model.*;
import com.risk.decision.repository.*;
import com.risk.enums.CalculationMethodType;
import com.risk.enums.DecisionStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    private final CalculationResultRepository calculationResultRepository;

    private final DecisionMapper decisionMapper;
    private final CalculationRequestMapper calculationRequestMapper;

    private final CalculationServiceClient calculationClient;
    private final CalculationResultPersistenceService persistenceService;
    private final DecisionResultProcessor resultProcessor;


    private final CalculationToDecisionMapper calcToDecisionMapper;



    @Transactional
    public DecisionResponse makeDecision(DecisionRequest request) {

        validationService.validateFactorDataConsistency(request);

        CalculationMethod methodEntity = validationService.validateMethodByName(calculationMethodRepository, request);


        Decision newDecision = decisionMapper.toEntity(request, methodEntity);

        List<Factor> factorEntities = newDecision.getFactors();
        if (factorEntities != null) {
            for (Factor f : factorEntities) {
                f.setDecision(newDecision);
            }
        }

        Map<String, Factor> factorByName = factorEntities
                .stream()
                .collect(Collectors.toMap(
                        f -> f.getName().toLowerCase(),
                        f -> f
                ));

        List<Alternative> alternativeEntities = newDecision.getAlternatives();

        Map<String, Alternative> alternativeByName = alternativeEntities
                .stream()
                .collect(Collectors.toMap(
                        a -> a.getName().toLowerCase(),
                        a -> a
                ));


        for (com.risk.decision.dto.Alternative altDto : request.alternatives()) {

            Alternative altEntity = alternativeByName.get(altDto.name().toLowerCase());

            if (altEntity == null)
                throw new IllegalStateException(
                        "Alternative '" + altDto.name() + "' not found in mapped entity list."
                );

            altEntity.setDecision(newDecision);

            List<Evaluation> evalEntities = altEntity.getEvaluations();
            List<EvaluationValue> evalDtos = altDto.values();

            if (evalEntities.size() != evalDtos.size()) {
                throw new IllegalStateException(
                        "Mismatch evaluations count for alternative '" + altDto.name() + "'"
                );
            }

            for (int i = 0; i < evalEntities.size(); i++) {
                Evaluation evalEntity = evalEntities.get(i);
                EvaluationValue evalDto = evalDtos.get(i);

                evalEntity.setAlternative(altEntity);

                Factor matchedFactor = factorByName.get(evalDto.factorName().toLowerCase());

                if (matchedFactor == null)
                    throw new IllegalStateException(
                            "Factor '" + evalDto.factorName() + "' not found for evaluation"
                    );

                evalEntity.setFactor(matchedFactor);
            }
        }

        Decision savedDecision = decisionRepository.save(newDecision);


        CalculationMethodType methodType = CalculationMethodType.valueOf(methodEntity.getName());
        CalculationRequest calcRequest = calculationRequestMapper.toCalculationRequest(savedDecision, methodType);

        log.info("Sending CalculationRequest to calculation-service: {}", calcRequest);

        CalculationResponse calcResponse = calculationClient.calculate(calcRequest);

        persistenceService.persistResults(calcResponse);

        //List<CalculationResult> calculationResults = calculationResultRepository.findByDecision_Id(savedDecision.getId());

        DecisionCalculationData calcData =
                calcToDecisionMapper.toDecisionCalculationData(
                        calcResponse,
                        savedDecision.getName()
                );


        DecisionResponse finalResponse = resultProcessor.makeFinalDecision(calcData);

        savedDecision.setStatus(DecisionStatus.FINALIZED);
        decisionRepository.save(savedDecision);

        return finalResponse;
    }

}
