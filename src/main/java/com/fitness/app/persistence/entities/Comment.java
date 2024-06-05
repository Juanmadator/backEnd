package com.fitness.app.persistence.entities;


import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long postId;
    private Long userId;
    private String content;

}
