package com.risk.decision.model;

import com.risk.enums.FactorClassification;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "factor", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"decision_id", "name"})
})
public class Factor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decision_id", nullable = false)
    private Decision decision;

    @Enumerated(EnumType.STRING)
    @Column(name = "factor_type", nullable = false, length = 20)
    private FactorClassification type;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "unit_of_measure", length = 50)
    private String unitOfMeasure;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_growing", columnDefinition = "boolean")
    private Boolean isGrowing;

    @Column(name = "factor_weight",nullable = false, precision = 5, scale = 2)
    private BigDecimal factorWeight;

    @OneToMany(mappedBy = "factor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Evaluation> evaluations;

}
