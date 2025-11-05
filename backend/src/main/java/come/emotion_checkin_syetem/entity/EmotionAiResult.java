package com.emotion_checkin_sytem.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "emotion_ai_result")
public class EmotionAiResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "checkin_id")
    private EmotionCheckin checkin;

    private float sentimentScore;
    private float magnitude;
    private String sentimentLabel;
    private String language;
    private LocalDateTime analyzedAt;
}