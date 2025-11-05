package com.emotion_checkin_sytem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "emotion_catalog")
public class EmotionCatalog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int level; // 1=Negative, 2=Neutral, 3=Positive
}