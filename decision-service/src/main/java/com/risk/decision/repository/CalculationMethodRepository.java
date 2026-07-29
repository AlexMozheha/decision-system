package com.risk.decision.repository;

import com.risk.decision.model.CalculationMethod;
import com.risk.enums.CalculationMethodType;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CalculationMethodRepository extends JpaRepository<CalculationMethod, Integer> {

    @Cacheable(value = "calculationMethods", key = "#name")
    Optional<CalculationMethod> findByName(CalculationMethodType name);
}
