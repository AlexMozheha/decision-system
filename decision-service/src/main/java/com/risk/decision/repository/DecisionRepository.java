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
import java.util.Optional;

@Repository
public interface DecisionRepository extends JpaRepository<Decision, Integer> {

    @Query(value = "SELECT d FROM Decision d " +
            "LEFT JOIN FETCH d.status " +
            "WHERE d.userId = :userId " +
            "AND d.status.name != com.risk.enums.DecisionStatus.READY_FOR_CALCULATION " +
            "AND LOWER(d.name) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))",
            countQuery = "SELECT COUNT(d) FROM Decision d " +
                    "WHERE d.userId = :userId " +
                    "AND d.status.name != com.risk.enums.DecisionStatus.READY_FOR_CALCULATION " +
                    "AND LOWER(d.name) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))")
    Page<Decision> findMyScenarios(
            @Param("userId") Integer userId,
            @Param("search") String search,
            Pageable pageable);

    @Query(value = "SELECT d FROM Decision d " +
            "LEFT JOIN FETCH d.status s " +
            "WHERE (:search IS NULL OR " +
            "       LOWER(d.name) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) OR " +
            "       (CAST(:userIds AS integer) IS NOT NULL AND d.userId IN :userIds)) " +
            "AND (COALESCE(:statuses, NULL) IS NULL OR s.name IN :statuses)",
            countQuery = "SELECT COUNT(d) FROM Decision d " +
                    "LEFT JOIN d.status s " +
                    "WHERE (:search IS NULL OR " +
                    "       LOWER(d.name) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) OR " +
                    "       (CAST(:userIds AS integer) IS NOT NULL AND d.userId IN :userIds)) " +
                    "AND (COALESCE(:statuses, NULL) IS NULL OR s.name IN :statuses)")
    Page<Decision> findAllScenariosForAdmin(
            @Param("search") String search,
            @Param("statuses") List<DecisionStatus> statuses,
            @Param("userIds") List<Integer> userIds,
            Pageable pageable);


    @Query("SELECT d FROM Decision d " +
            "LEFT JOIN FETCH d.calculationResults " +
            "WHERE d.id = :decisionId")
    Optional<Decision> findResultById(@Param("decisionId") Integer decisionId);

    @Query("SELECT d FROM Decision d " +
            "LEFT JOIN FETCH d.alternatives a " +
            "WHERE d.id = :decisionId")
    Optional<Decision> findAlternativesByDecisionId(@Param("decisionId") Integer decisionId);
}
