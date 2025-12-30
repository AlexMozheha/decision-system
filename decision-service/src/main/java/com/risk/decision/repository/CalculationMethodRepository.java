package com.risk.decision.repository;

import com.risk.decision.model.CalculationMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CalculationMethodRepository extends JpaRepository<CalculationMethod, Integer> {

    Optional<CalculationMethod> findByName(String name);
}
