package come.emotion_checkin_syetem.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 😊 Emotion Check-in Entity - บันทึกการ check-in อารมณ์ของพนักงาน
 * 
 * กฎสำคัญ:
 * 1. จำกัด 1 check-in ต่อวัน ต่อพนักงาน (unique constraint)
 * 2. เก็บ raw comment (เห็นได้แค่ employee + superadmin)
 * 3. บันทึกเวลาเป็น Thailand timezone
 * 4. ถ้าอารมณ์แย่ (level 1) → ส่ง notification ให้ HR
 * 
 * Fields:
 * - emotionLevel: 1=Negative, 2=Neutral, 3=Positive
 * - comment: ข้อความส่วนตัว (private!)
 * - checkinDate: วันที่ check-in (สำหรับ unique constraint)
 * - checkinTime: เวลาที่ check-in (Thailand timezone)
 */
@Entity
@Table(name = "emotion_checkin",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_employee_date", columnNames = {"employee_id", "checkin_date"})
    },
    indexes = {
        @Index(name = "idx_checkin_date", columnList = "checkin_date"),
        @Index(name = "idx_employee_date", columnList = "employee_id, checkin_date")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmotionCheckin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Employee ที่ check-in
     * - Lazy loading เพื่อ performance
     * - Cascade: ถ้าลบ user ลบ check-in ด้วย
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;

    /**
     * Emotion Level:
     * 1 = Negative (Sad, Angry, Stressed, Anxious, Bored)
     * 2 = Neutral (Calm, Tired, Indifferent, Focused, Uncertain)
     * 3 = Positive (Happy, Relaxed, Excited, Proud, Motivated)
     */
    @Column(nullable = false)
    private Integer emotionLevel;

    /**
     * Link ไปหา EmotionCatalog (optional)
     * - เก็บว่าเลือก emotion อะไร (Happy, Sad, etc.)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emotion_type_id")
    private EmotionCatalog emotionType;

    /**
     * Raw Comment - ความรู้สึกเพิ่มเติม
     * - PRIVATE: ดูได้แค่ employee ที่เขียน + superadmin
     * - HR ห้ามดู!
     * - ใช้ Google NLP วิเคราะห์ sentiment
     */
    @Column(columnDefinition = "TEXT")
    private String comment;

    /**
     * เวลาที่ check-in (Thailand timezone)
     * - Auto-generate ตอน save
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime checkinTime;

    /**
     * วันที่ check-in (สำหรับ unique constraint)
     * - ใช้เช็คว่าวันนี้ check-in แล้วหรือยัง
     */
    @Column(name = "checkin_date", nullable = false)
    private LocalDate checkinDate;

    // ========== Relationship ==========

    /**
     * AI Analysis Result (1-to-1)
     * - เก็บผลจาก Google NLP API
     * - Cascade: ลบ check-in → ลบ AI result ด้วย
     */
    @OneToOne(mappedBy = "checkin", cascade = CascadeType.ALL, orphanRemoval = true)
    private EmotionAIResult aiResult;

    // ========== Helper Methods ==========

    /**
     * เช็คว่าอารมณ์แย่หรือไม่
     * Level 1 = Negative mood
     */
    public boolean isBadMood() {
        return emotionLevel == 1;
    }

    /**
     * เช็คว่าต้องแจ้งเตือน HR หรือไม่
     * ถ้าอารมณ์แย่ → แจ้ง HR
     */
    public boolean shouldNotifyHR() {
        return isBadMood();
    }

    /**
     * Set checkin date จาก checkin time
     * เรียกอัตโนมัติก่อน save
     */
    @PrePersist
    public void prePersist() {
        if (checkinDate == null && checkinTime != null) {
            checkinDate = checkinTime.toLocalDate();
        }
    }

    @Override
    public String toString() {
        return "EmotionCheckin{" +
                "id=" + id +
                ", employeeId=" + (employee != null ? employee.getId() : null) +
                ", emotionLevel=" + emotionLevel +
                ", checkinDate=" + checkinDate +
                ", checkinTime=" + checkinTime +
                '}';
    }
}