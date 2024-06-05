package com.fitness.app.persistence.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "user_group")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "user_id")
    private Long userId;


    @Column(name = "group_id")
    private Long groupId;


}
