package com.risk.decision.repository;

import com.risk.api.dto.AltCalculationDto;
import com.risk.decision.model.Alternative;
import com.risk.api.dto.CalculationRequest;
import com.risk.api.dto.EvaluationValue;
import com.risk.api.dto.FactorParams;
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
    CalculationRequest toCalculationRequest(Decision decision, CalculationMethodType methodType);


    // Factor Entity -> FactorParams DTO
    @Mapping(target = "factorId", source = "id")
    FactorParams toFactorParams(Factor factor);


    // Alternative Entity -> Alternative DTO
    @Mapping(target = "alternativeId", source = "id")
    @Mapping(target = "values", source = "evaluations")
    AltCalculationDto toAlternativeDto(Alternative alternative);


    // Evaluation Entity -> EvaluationValue DTO
    @Mapping(target = "factorId", source = "factor.id")
    @Mapping(target = "rawValue", source = "rawValue")
    EvaluationValue toEvaluationValue(Evaluation evaluation);
}
