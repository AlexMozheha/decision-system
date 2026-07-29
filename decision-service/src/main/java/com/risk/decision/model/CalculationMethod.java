package com.risk.decision.model;

import com.risk.enums.CalculationMethodType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "calculation_method")
public class CalculationMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "method_id", nullable = false)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private CalculationMethodType name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

}
