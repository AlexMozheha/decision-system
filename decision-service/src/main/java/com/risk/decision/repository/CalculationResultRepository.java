package com.risk.decision.repository;

import com.risk.decision.model.CalculationResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CalculationResultRepository extends JpaRepository<CalculationResult, Integer> {

    List<CalculationResult> findByDecision_Id(Integer decisionId);
}
