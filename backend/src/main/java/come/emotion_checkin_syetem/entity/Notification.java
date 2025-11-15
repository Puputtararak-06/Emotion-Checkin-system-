package come.emotion_checkin_syetem.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 🔔 Notification Entity - ระบบแจ้งเตือน
 * 
 * 2 ประเภท:
 * 1. System Notification: ระบบแจ้ง HR เมื่อพนักงานอารมณ์แย่
 * 2. HR Notification: HR ส่งข้อความหาพนักงาน
 * 
 * Flow:
 * 1. พนักงาน check-in อารมณ์แย่ (level 1)
 * 2. ระบบ auto-create notification ให้ HR
 * 3. HR เห็น notification → ส่งข้อความหาพนักงาน
 * 4. พนักงานเห็น notification จาก HR
 * 
 * Features:
 * - Read/Unread status
 * - Time elapsed ("5 minutes ago")
 * - Link to related check-in (optional)
 */
@Entity
@Table(name = "notification",
    indexes = {
        @Index(name = "idx_receiver", columnList = "receiver_id"),
        @Index(name = "idx_read_status", columnList = "readStatus"),
        @Index(name = "idx_created_at", columnList = "createdAt")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ผู้ส่ง
     * - System notification: sender = employee (who checked in)
     * - HR notification: sender = HR
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    /**
     * ผู้รับ
     * - System notification: receiver = HR
     * - HR notification: receiver = employee
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    /**
     * ข้อความ
     * ตัวอย่าง:
     * - "Employee John reported a negative mood. Please follow up."
     * - "Please schedule a meeting with me this week."
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * สถานะการอ่าน
     * - false: ยังไม่อ่าน (แสดง badge แดง)
     * - true: อ่านแล้ว
     */
    @Column(nullable = false)
    private Boolean readStatus = false;
 
    /**
     * Link ไป check-in ที่เกี่ยวข้อง (optional)
     * - System notification: link ไป check-in ที่ trigger notification
     * - HR notification: อาจจะ link หรือไม่ link ก็ได้
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_checkin_id")
    private EmotionCheckin relatedCheckin;

    // ========== Helper Methods ==========

    /**
     * Mark as read
     */
    public void markAsRead() {
        this.readStatus = true;
    }

    /**
     * Mark as unread
     */
    public void markAsUnread() {
        this.readStatus = false;
    }

    /**
     * เช็คว่าเป็น notification จาก HR หรือไม่
     */
    public boolean isFromHR() {
        return sender != null && sender.isHR();
    }

    /**
     * เช็คว่าเป็น system notification หรือไม่
     * (auto-generated เมื่อพนักงานอารมณ์แย่)
     */
    public boolean isSystemNotification() {
        return relatedCheckin != null && relatedCheckin.isBadMood();
    }

    /**
     * คำนวณเวลาที่ผ่านไป (สำหรับแสดงใน UI)
     * - "เมื่อสักครู่" (< 1 minute)
     * - "5 นาทีที่แล้ว"
     * - "2 ชั่วโมงที่แล้ว"
     * - "3 วันที่แล้ว"
     */
    public String getTimeElapsed() {
        if (createdAt == null) return "";
        
        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(createdAt, now).toMinutes();
        
        if (minutes < 1) return "เมื่อสักครู่";
        if (minutes < 60) return minutes + " นาทีที่แล้ว";
        
        long hours = minutes / 60;
        if (hours < 24) return hours + " ชั่วโมงที่แล้ว";
        
        long days = hours / 24;
        return days + " วันที่แล้ว";
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", senderId=" + (sender != null ? sender.getId() : null) +
                ", receiverId=" + (receiver != null ? receiver.getId() : null) +
                ", readStatus=" + readStatus +
                ", createdAt=" + createdAt +
                '}';
    }
}