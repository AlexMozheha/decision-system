package com.risk.decision.repository;


import com.risk.decision.dto.*;
import com.risk.decision.model.Alternative;
import com.risk.decision.model.CalculationMethod;
import com.risk.decision.model.Decision;
import com.risk.decision.model.Evaluation;
import com.risk.decision.model.Factor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DecisionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "name", source = "request.decisionName")
    @Mapping(target = "calculationMethod", source = "method")
    @Mapping(target = "factors", source = "request.factorParams")
    @Mapping(target = "alternatives", source = "request.alternativeRequests")
    Decision toEntity(DecisionRequest request, CalculationMethod method);

    // Alternative DTO (Source) -> Alternative Entity (Target)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "decision", ignore = true)
    @Mapping(target = "calculationResults", ignore = true)
    @Mapping(target = "evaluations", ignore = true)
    Alternative toAlternative(AlternativeRequest alternativeRequestDto);

    // FactorParams DTO (Source) -> Factor Entity (Target)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "decision", ignore = true)
    @Mapping(target = "evaluations", ignore = true)
    Factor toFactor(FactorParams factorParams);

    // EvaluationValue DTO (Source) -> Evaluation Entity (Target)
    @Mapping(target = "alternative", ignore = true)
    @Mapping(target = "factor", ignore = true)
    Evaluation toEvaluation(EvaluationValue evaluationValue);

    // ENTITY -> RESPONSE
    DecisionResponse toResponse(Decision decision);

    DecisionDraftResponse toDraftResponse(Decision decision);

    @Mapping(target = "statusName", source = "status.name")
    InvestorScenarioDto toInvestorScenarioDto(Decision decision);

    @Mapping(target = "riskCoefficient", expression = "java(alternative.getRiskCoefficient() != null ? alternative.getRiskCoefficient().multiply(new java.math.BigDecimal(\"100\")).intValue() : 0)")
    DraftAlternativeDto toDraftAlternativeDto(Alternative alternative);

    @Mapping(target = "riskCoefficient", expression = "java(alternative.getRiskCoefficient() != null ? alternative.getRiskCoefficient().multiply(new java.math.BigDecimal(\"100\")).intValue() : 0)")
    List<DraftAlternativeDto> toDraftAlternativeDtoList(List<Alternative> alternatives);

    OrderToCalculation toOrderToCalculation(
            Decision decision,
            String investorName,
            String investorEmail,
            String statusName
    );

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "name", source = "request.decisionName")
    @Mapping(target = "calculationMethod", source = "method")
    @Mapping(target = "factors", ignore = true)
    @Mapping(target = "alternatives", ignore = true)
    void updateEntityFromRequest(DecisionRequest request, CalculationMethod method, @MappingTarget Decision decision);
}
