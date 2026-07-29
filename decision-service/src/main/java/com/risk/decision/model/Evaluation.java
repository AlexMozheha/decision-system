package com.risk.decision.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(EvaluationId.class)
@Table(name = "evaluation")
public class Evaluation {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alt_id", nullable = false)
    private Alternative alternative;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fac_id", nullable = false)
    private Factor factor;

    @Column(name = "raw_value", precision = 15, scale = 2)
    private BigDecimal rawValue;

    @Column(name = "score", precision = 10, scale = 4)
    private BigDecimal score;

}
