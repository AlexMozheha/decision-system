package com.risk.decision.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "alternative")
public class Alternative {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decision_id", nullable = false)
    private Decision decision;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "risk_coefficient", precision = 3, scale = 2)
    private BigDecimal riskCoefficient;

    @OneToMany(mappedBy = "alternative", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Evaluation> evaluations;

    @OneToOne(mappedBy = "alternative", cascade = CascadeType.ALL, orphanRemoval = true)
    private CalculationResult calculationResult;

}