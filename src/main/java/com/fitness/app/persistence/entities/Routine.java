package com.fitness.app.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "routine")
public class Routine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "routine")
    private String routine;

    @Column(name = "grupo_muscular")
    private String grupoMuscular;


    @Column(name = "repeticiones")
    private int repeticiones;

    @Column(name = "peso")
    private int peso;

    @Column(name = "tiempo")
    private int tiempo;

    @Column(name = "kilometros")
    private int kilometros;

    @Column(name = "date_created")
    private LocalDateTime dateCreated;

}
