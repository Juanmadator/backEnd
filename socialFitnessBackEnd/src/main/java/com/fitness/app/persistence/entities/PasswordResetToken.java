package com.fitness.app.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


    @Entity
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public class PasswordResetToken {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "user_id", nullable = false)
        private User user;

        @Column(nullable = false, unique = true)
        private String token;

        @Column(nullable = false)
        private LocalDateTime expiryDate;
    }

