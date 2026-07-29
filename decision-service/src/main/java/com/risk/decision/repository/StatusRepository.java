package com.risk.decision.repository;

import com.risk.decision.model.Status;
import com.risk.enums.DecisionStatus;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatusRepository extends JpaRepository<Status,Integer> {

    @Cacheable(value = "statuses", key = "#name")
    Optional<Status> findByName(DecisionStatus name);

}
