package come.emotion_checkin_syetem.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 🤖 Emotion AI Result Entity - เก็บผลวิเคราะห์จาก Google NLP API
 * 
 * Google Cloud Natural Language API วิเคราะห์ comment แล้วได้:
 * 1. Sentiment Score: -1.0 (แย่มาก) ถึง 1.0 (ดีมาก)
 * 2. Magnitude: ความแรงของอารมณ์ (0.0 ถึง infinity)
 * 3. Sentiment Label: POSITIVE, NEUTRAL, NEGATIVE
 * 4. Language: th (ไทย) หรือ en (อังกฤษ)
 * 
 * ใช้ทำอะไร?
 * - วิเคราะห์ความรู้สึกจริงจาก comment
 * - เปรียบเทียบกับ emotion level ที่เลือก
 * - ตรวจจับความเสี่ยง (very negative + high magnitude)
 */
@Entity
@Table(name = "emotion_ai_result",
    indexes = {
        @Index(name = "idx_checkin", columnList = "checkin_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmotionAIResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Link ไป Check-in (1-to-1)
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checkin_id", nullable = false)
    private EmotionCheckin checkin;

    /**
     * Sentiment Score จาก Google NLP
     * - Range: -1.0 (very negative) ถึง 1.0 (very positive)
     * - 0.0 = neutral
     * 
     * ตัวอย่าง:
     * - "I love this!" → +0.9
     * - "It's okay" → 0.0
     * - "I hate this" → -0.8
     */
    @Column(nullable = false)
    private Float sentimentScore;

    /**
     * Magnitude - ความแรงของอารมณ์
     * - Range: 0.0 ถึง infinity
     * - ยิ่งสูง = อารมณ์ยิ่งแรง (ไม่ว่าจะบวกหรือลบ)
     * 
     * ตัวอย่าง:
     * - "Good" → magnitude = 0.3
     * - "Absolutely amazing!" → magnitude = 2.5
     * - "Terrible, awful, horrible" → magnitude = 3.0
     */
    @Column(nullable = false)
    private Float magnitude;

    /**
     * Sentiment Label - แปลง score เป็น label
     * - POSITIVE: score > 0.25
     * - NEUTRAL: score ระหว่าง -0.25 ถึง 0.25
     * - NEGATIVE: score < -0.25
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private SentimentLabel sentimentLabel;

    /**
     * ภาษาที่ตรวจพบ
     * - th = ไทย
     * - en = อังกฤษ
     */
    @Column(length = 10)
    private String language;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime analyzedAt;

    // ========== Enums ==========

    public enum SentimentLabel {
        POSITIVE,   // บวก (score > 0.25)
        NEUTRAL,    // กลางๆ (score -0.25 ถึง 0.25)
        NEGATIVE    // ลบ (score < -0.25)
    }

    // ========== Helper Methods ==========

    /**
     * แปลง sentiment score เป็น label
     */
    public static SentimentLabel determineSentimentLabel(float score) {
        if (score > 0.25) return SentimentLabel.POSITIVE;
        if (score < -0.25) return SentimentLabel.NEGATIVE;
        return SentimentLabel.NEUTRAL;
    }

    /**
     * เช็คว่า AI sentiment ตรงกับ emotion level ที่เลือกไหม
     * 
     * ควรตรง:
     * - Level 1 (Negative) ↔ NEGATIVE sentiment
     * - Level 2 (Neutral) ↔ NEUTRAL sentiment  
     * - Level 3 (Positive) ↔ POSITIVE sentiment
     * 
     * ถ้าไม่ตรง = พนักงานอาจจะซ่อนความรู้สึกจริง
     */
    public boolean matchesEmotionLevel() {
        if (checkin == null) return false;
        
        int level = checkin.getEmotionLevel();
        
        // Level 1 (Negative) should match NEGATIVE
        if (level == 1) return sentimentLabel == SentimentLabel.NEGATIVE;
        
        // Level 2 (Neutral) should match NEUTRAL
        if (level == 2) return sentimentLabel == SentimentLabel.NEUTRAL;
        
        // Level 3 (Positive) should match POSITIVE
        if (level == 3) return sentimentLabel == SentimentLabel.POSITIVE;
        
        return false;
    }

    /**
     * เช็คว่าเป็นความเสี่ยงสูงหรือไม่
     * - Very negative sentiment (< -0.5)
     * - High magnitude (> 2.0)
     * → ควรแจ้ง HR ทันที!
     */
    public boolean isHighRiskMood() {
        return sentimentScore < -0.5 && magnitude > 2.0;
    }

    @Override
    public String toString() {
        return "EmotionAIResult{" +
                "id=" + id +
                ", sentimentScore=" + sentimentScore +
                ", magnitude=" + magnitude +
                ", sentimentLabel=" + sentimentLabel +
                ", language='" + language + '\'' +
                ", analyzedAt=" + analyzedAt +
                '}';
    }
}