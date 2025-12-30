package com.risk.decision.repository;

import com.risk.decision.model.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluationRepository extends JpaRepository<Evaluation, Integer> {

    List<Evaluation> findByAlternative_Id(Integer alternativeId);
    List<Evaluation> findByFactorName(String factorName);
}
