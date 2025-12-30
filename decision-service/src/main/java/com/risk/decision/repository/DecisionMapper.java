package com.risk.decision.repository;


import com.risk.decision.dto.*;
import com.risk.decision.model.CalculationMethod;
import com.risk.decision.model.Decision;
import com.risk.decision.model.Evaluation;
import com.risk.decision.model.Factor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DecisionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status", constant = "DRAFT")
    @Mapping(target = "name", source = "request.decisionName")
    @Mapping(target = "calculationMethod", source = "method")
    @Mapping(target = "factors", source = "request.factorParams")
    @Mapping(target = "alternatives", source = "request.alternatives")
    Decision toEntity(DecisionRequest request, CalculationMethod method);

    // Alternative DTO (Source) -> Alternative Entity (Target)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "decision", ignore = true)
    @Mapping(target = "calculationResult", ignore = true)
    @Mapping(target = "evaluations", source = "values")
    com.risk.decision.model.Alternative toAlternative(com.risk.decision.dto.Alternative alternativeDto);

    // FactorParams DTO (Source) -> Factor Entity (Target)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "decision", ignore = true)
    @Mapping(target = "evaluations", ignore = true)
    @Mapping(target = "factorWeight", source = "factorWeight")
    @Mapping(target = "isGrowing", source = "isGrowing")
    Factor toFactor(FactorParams factorParams);

    // EvaluationValue DTO (Source) -> Evaluation Entity (Target)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "alternative", ignore = true)
    @Mapping(target = "factor", ignore = true)
    Evaluation toEvaluation(EvaluationValue evaluationValue);

    // ENTITY -> RESPONSE
    DecisionResponse toResponse(Decision decision);

}
