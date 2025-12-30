package com.risk.decision.repository;


import com.risk.decision.model.Alternative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlternativeRepository  extends JpaRepository<Alternative, Integer> {

    List<Alternative> findByDecision_Id(Integer decisionId);
}
