package com.risk.decision.repository;


import com.risk.decision.model.Decision;
import com.risk.enums.DecisionStatus;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DecisionRepository extends JpaRepository<Decision, Integer> {

    @Query("SELECT d FROM Decision d " +
            "LEFT JOIN FETCH d.status " +
            "LEFT JOIN FETCH d.calculationResults " +
            "WHERE d.userId = :userId " +
            "AND LOWER(d.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Decision> findMyScenarios(
            @Param("userId") Integer userId,
            @Param("search") String search,
            Pageable pageable);

    @Query("SELECT d FROM Decision d " +
            "LEFT JOIN FETCH d.status s " +
            "LEFT JOIN FETCH d.calculationResults " +
            "WHERE (:search IS NULL OR " +
            "       LOWER(d.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "       (COALESCE(:userIds, NULL) IS NOT NULL AND d.userId IN :userIds)) " +
            "AND (:status IS NULL OR s.name = :status)")
    Page<Decision> findAllScenariosForAdmin(
            @Param("search") String search,
            @Param("status") DecisionStatus status,
            @Param("userIds") List<Integer> userIds,
            Pageable pageable);
}
