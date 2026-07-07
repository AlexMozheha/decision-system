package com.risk.decision.repository;

import com.risk.decision.model.CalculationResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CalculationResultRepository extends JpaRepository<CalculationResult, Integer> {

    List<CalculationResult> findByDecision_Id(Integer decisionId);

    @Query("SELECT cr FROM CalculationResult cr " +
            "JOIN FETCH cr.alternative " +
            "WHERE cr.decision.id = :decisionId")
    List<CalculationResult> findResultsWithAlternativeByDecisionId(@Param("decisionId") Integer decisionId);
}
