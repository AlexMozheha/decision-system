package com.risk.decision.model;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode
public class EvaluationId implements Serializable {
    private Integer alternative; 
    private Integer factor; 
}