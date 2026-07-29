package com.risk.decision.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "alternative")
public class Alternative {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alt_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dec_id", nullable = false)
    private Decision decision;

    @Column(name = "alternative_name", nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "risk_coefficient", precision = 3, scale = 2)
    private BigDecimal riskCoefficient;

    @OneToMany(mappedBy = "alternative", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Evaluation> evaluations;

    @OneToMany(mappedBy = "alternative", orphanRemoval = true)
    private List<CalculationResult> calculationResults;

}