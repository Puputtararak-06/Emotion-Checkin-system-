package come.emotion_checkin_syetem.entity;

import jakarta.persistence.*;
import lombok.*;

import org.checkerframework.checker.units.qual.s;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 📝 Audit Log Entity - บันทึกการกระทำทั้งหมดในระบบ
 *
 * ทำไมต้องมี Audit Log?
 * 1. ตามกฎหมาย PDPA: ต้องเก็บ log การเข้าถึงข้อมูลส่วนบุคคล
 * 2. Security: ตรวจสอบได้ว่าใครทำอะไร เมื่อไหร่
 * 3. Debugging: เช็คประวัติเมื่อเกิดปัญหา
 * 4. Analytics: วิเคราะห์การใช้งานระบบ
 */
@Entity
@Table(name = "audit_log",
    indexes = {
        @Index(name = "idx_user_action", columnList = "user_id, action"),
        @Index(name = "idx_created_at", columnList = "created_at"),
        @Index(name = "idx_target_user", columnList = "target_user_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** ผู้ที่ทำการกระทำ */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)   // ← FIXED
    private User user;

    /** ประเภทของการกระทำ */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private Action action;

    /** user เป้าหมายของการกระทำ */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id")               // ← FIXED
    private User targetUser;

    /** รายละเอียด */
    @Column(columnDefinition = "TEXT")
    private String details;

    /** IP Address */
    @Column(name = "ip_address", length = 45)          // ← FIXED
    private String ipAddress;

    /** เวลาที่สร้าง log */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)    // ← FIXED
    private LocalDateTime createdAt;

    /** flag สำคัญ */
    @Column(name = "is_critical")                      // ← FIXED
    private Boolean isCritical;

    /** authentication action? */
    @Column(name = "is_auth_action")                   // ← FIXED
    private Boolean isAuthAction;

    // ==============================================================
    // ENUM
    // ==============================================================

    public enum Action {
        LOGIN("เข้าสู่ระบบ"),
        LOGOUT("ออกจากระบบ"),
        REGISTER("ลงทะเบียน"),
        LOGIN_FAILED("เข้าสู่ระบบล้มเหลว"),

        CHECK_IN("เช็คอินอารมณ์"),
        VIEW_DASHBOARD("ดู Dashboard"),

        VIEW_EMPLOYEE_INSIGHT("ดูข้อมูลพนักงาน"),
        ASSIGN_DEPARTMENT("กำหนดแผนก"),
        SEND_NOTIFICATION("ส่ง Notification"),

        ADD_USER("เพิ่มผู้ใช้"),
        EDIT_USER("แก้ไขผู้ใช้"),
        DEACTIVATE_USER("ปิดการใช้งาน"),
        ACTIVATE_USER("เปิดการใช้งาน"),
        VIEW_AUDIT_LOG("ดู Audit Log"),

        // ถ้าไม่ใช้สองอันนี้ จะลบก็ได้
        PASSWORD_CHANGE("เปลี่ยนรหัสผ่าน"),
        PROFILE_UPDATE("อัพเดทโปรไฟล์");

        private final String description;

        Action(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }


    public static String createDetails(String key, Object value) {
        // Safely build a simple JSON-like details string.
        // Use String.valueOf(value) to avoid NPE and ensure non-string values are handled.
        String val = value == null ? "null" : String.valueOf(value).replace("\"", "\\\"");
        return String.format("{\"%s\":\"%s\"}", key, val);
    }

    public boolean isCriticalAction() {
        return action == Action.ADD_USER ||
               action == Action.DEACTIVATE_USER ||
               action == Action.ASSIGN_DEPARTMENT ||
               action == Action.PASSWORD_CHANGE;
    }

    public boolean isAuthAction() {
        return action == Action.LOGIN ||
               action == Action.LOGOUT ||
               action == Action.REGISTER ||
               action == Action.LOGIN_FAILED;
    }

    public boolean requiresTargetUser() {
        return action == Action.VIEW_EMPLOYEE_INSIGHT ||
               action == Action.ASSIGN_DEPARTMENT ||
               action == Action.ADD_USER ||
               action == Action.EDIT_USER ||
               action == Action.DEACTIVATE_USER ||
               action == Action.ACTIVATE_USER ||
               action == Action.SEND_NOTIFICATION;
    }

    public String getFormattedDescription() {
        String desc = action.getDescription();
        
        if (targetUser != null) {
            desc += " (" + targetUser.getName() + ")";
        }
        
        return desc;
    }

    @Override
    public String toString() {
        return "AuditLog{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : null) +
                ", action=" + action +
                ", targetUserId=" + (targetUser != null ? targetUser.getId() : null) +
                ", ipAddress='" + ipAddress + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}