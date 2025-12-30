package com.risk.decision.repository;


import com.risk.decision.model.Factor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FactorRepository extends JpaRepository<Factor, Integer> {

    List<Factor> findByDecision_Id(Integer decisionId);

}
