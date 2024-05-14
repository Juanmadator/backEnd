package com.fitness.app.persistence.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "group_message")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne
    @JoinColumn(name = "sender_user_id")
    private User sender;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "file_url")
    private String fileUrl;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_sent")
    private Date dateSent;
}
