package com.risk.decision.repository;

import com.risk.decision.model.Evaluation;
import com.risk.decision.model.EvaluationId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluationRepository extends JpaRepository<Evaluation, EvaluationId> {

    List<Evaluation> findByAlternative_Id(Integer alternativeId);
    List<Evaluation> findByFactor_Id(Integer factorId);

}
