package com.risk.decision.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "calculation_result")
public class CalculationResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decision_id", nullable = false)
    private Decision decision;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alternative_id", nullable = false, unique = true)
    private Alternative alternative;

    @Column(name = "weighted_score", precision = 10, scale = 4)
    private BigDecimal weightedScore;

    @Column(name = "risk_adjusted_score", precision = 10, scale = 4)
    private BigDecimal riskAdjustedScore;

    @Column(name = "risk_lvl", length = 50)
    private String riskLevel;

    @Column(name = "calculated_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime calculatedAt = ZonedDateTime.now();
}
