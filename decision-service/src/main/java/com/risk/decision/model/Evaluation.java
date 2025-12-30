package com.risk.decision.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "evaluation", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"alternative_id", "factor_id"})
})
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alternative_id", nullable = false)
    private Alternative alternative;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factor_id", nullable = false)
    private Factor factor;

    @Column(name = "raw_value", precision = 15, scale = 2)
    private BigDecimal rawValue;

    @Column(precision = 5, scale = 2)
    private BigDecimal score;

}
