package com.risk.decision.repository;

import com.risk.dto.Alternative;
import com.risk.dto.CalculationRequest;
import com.risk.dto.EvaluationValue;
import com.risk.dto.FactorParams;
import com.risk.decision.model.Decision;
import com.risk.decision.model.Evaluation;
import com.risk.decision.model.Factor;
import com.risk.enums.CalculationMethodType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CalculationRequestMapper {

    @Mapping(target = "decisionId", source = "decision.id")
    @Mapping(target = "method", source = "methodType")
    @Mapping(target = "maxScore", source = "decision.maxScore")
    @Mapping(target = "factorParams", source = "decision.factors")
    @Mapping(target = "alternatives", source = "decision.alternatives")
    CalculationRequest toCalculationRequest(Decision decision, CalculationMethodType methodType);


    // Factor Entity -> FactorParams DTO
    @Mapping(target = "factorId", source = "id")
    @Mapping(target = "factorWeight", source = "factorWeight")
    @Mapping(target = "isGrowing", source = "isGrowing")
    FactorParams toFactorParams(Factor factor);


    // Alternative Entity -> Alternative DTO
    @Mapping(target = "alternativeId", source = "id")
    @Mapping(target = "riskCoefficient", source = "riskCoefficient")
    @Mapping(target = "values", source = "evaluations")
    Alternative toAlternativeDto(com.risk.decision.model.Alternative alternative);


    // Evaluation Entity -> EvaluationValue DTO
    @Mapping(target = "factorId", source = "factor.id")
    @Mapping(target = "rawValue", source = "rawValue")
    @Mapping(target = "score", source = "score")
    EvaluationValue toEvaluationValue(Evaluation evaluation);
}
