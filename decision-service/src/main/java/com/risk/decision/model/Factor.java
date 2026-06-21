package com.risk.decision.model;

import com.risk.enums.FactorClassification;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "factor")
public class Factor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fac_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dec_id", nullable = false)
    private Decision decision;

    @Enumerated(EnumType.STRING)
    @Column(name = "fac_type", nullable = false)
    private FactorClassification type;

    @Column(name = "fac_name", nullable = false, length = 100)
    private String name;

    @Column(name = "unit_of_measure", length = 50)
    private String unitOfMeasure;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_growing", columnDefinition = "boolean default true")
    private Boolean isGrowing;

    @Column(name = "factor_weight",nullable = false, precision = 5, scale = 2)
    private BigDecimal factorWeight;

    @OneToMany(mappedBy = "factor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Evaluation> evaluations;

}
