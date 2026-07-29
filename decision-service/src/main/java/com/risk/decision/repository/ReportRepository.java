package com.risk.decision.repository;
import com.risk.decision.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report,Integer> {

    Optional<Report> findByDecision_Id(Integer decisionId);
}
