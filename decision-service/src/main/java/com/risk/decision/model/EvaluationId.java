package com.risk.decision.model;

import java.io.Serializable;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class EvaluationId implements Serializable {
    private Integer alternative; 
    private Integer factor; 
}