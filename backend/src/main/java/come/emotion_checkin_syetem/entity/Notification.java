package com.emotion_checkin_sytem.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notification")   
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;
    private LocalDateTime createdAt;
    private boolean readStatus = false;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private HR sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Employee receiver;
}