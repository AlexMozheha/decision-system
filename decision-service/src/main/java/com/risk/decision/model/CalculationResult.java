package com.risk.decision.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "calculation_result")
public class CalculationResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dec_id", nullable = false)
    private Decision decision;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alt_id", nullable = false)
    private Alternative alternative;

    @Column(name = "weighted_score", precision = 10, scale = 4)
    private BigDecimal weightedScore;

    @Column(name = "risk_adjusted_score", precision = 10, scale = 4)
    private BigDecimal riskAdjustedScore;

    @Column(name = "risk_lvl", length = 50)
    private String riskLevel;

    @Builder.Default
    @Column(name = "calculated_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime calculatedAt = ZonedDateTime.now();
}
