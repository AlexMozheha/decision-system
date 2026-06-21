package com.risk.user.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="role_id", nullable = false)
    private Integer id;

    @Column(name = "role_name", nullable = false, unique = true, length = 30)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
