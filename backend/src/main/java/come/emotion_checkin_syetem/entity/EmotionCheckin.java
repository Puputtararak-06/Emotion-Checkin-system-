package com.emotion_checkin_sytem.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "emotion_checkin")
public class EmotionCheckin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int emotionLevel;

    @ManyToOne
    @JoinColumn(name = "emotion_type_id")
    private EmotionCatalog emotionType;

    @Column(columnDefinition = "TEXT")
    private String comment;

    private LocalDateTime checkinTime;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
}