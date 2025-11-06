package come.emotion_checkin_syetem.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 🎭 Emotion Catalog Entity - ตารางเก็บรายการอารมณ์ทั้งหมด
 * 
 * จากตารางที่มึงส่งมา มี 3 Levels:
 * - Level 1 (Negative/ลบ): Sad, Angry, Stressed, Anxious, Bored
 * - Level 2 (Neutral/กลางๆ): Calm, Tired, Indifferent, Focused, Uncertain  
 * - Level 3 (Positive/บวก): Happy, Relaxed, Excited, Proud, Motivated
 * 
 * ใช้ทำอะไร?
 * - เก็บ master data ของอารมณ์
 * - แสดงให้ user เลือกตอน check-in
 * - เก็บคำอธิบายภาษาไทย + สี UI
 */
@Entity
@Table(name = "emotion_catalog",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_name_level", columnNames = {"name", "level"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmotionCatalog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name; // Happy, Sad, Angry, Calm, etc.

    /**
     * Emotion Level:
     * 1 = Negative (ลบ/แย่)
     * 2 = Neutral (กลางๆ)
     * 3 = Positive (บวก/ดี)
     */
    @Column(nullable = false)
    private Integer level;

    @Column(columnDefinition = "TEXT")
    private String description; // คำอธิบายภาษาไทย

    /**
     * Color code สำหรับ UI (HEX format)
     * - Level 1 (Negative): สีแดง/ส้ม #F44336
     * - Level 2 (Neutral): สีเหลือง/ส้ม #FFC107
     * - Level 3 (Positive): สีเขียว #4CAF50
     */
    @Column(length = 7)
    private String colorCode;

    // ========== Helper Methods ==========

    public boolean isNegativeMood() {
        return level == 1;
    }

    public boolean isNeutralMood() {
        return level == 2;
    }

    public boolean isPositiveMood() {
        return level == 3;
    }

    @Override
    public String toString() {
        return "EmotionCatalog{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", level=" + level +
                ", description='" + description + '\'' +
                ", colorCode='" + colorCode + '\'' +
                '}';
    }
}