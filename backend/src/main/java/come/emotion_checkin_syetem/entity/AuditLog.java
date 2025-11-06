package com.emotion.checkin.entity;

import jakarta.persistence.*;
import lombok.*;
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
 * 
 * บันทึกอะไรบ้าง?
 * - การ login/logout
 * - การ check-in
 * - การ assign department
 * - การ add/edit/deactivate user
 * - การดูข้อมูลพนักงาน (employee insight)
 * - การส่ง notification
 * 
 * สำหรับ SuperAdmin:
 * - ดู audit log ได้ทั้งหมด
 * - Search by user, role, action
 * - Filter by date range
 */
@Entity
@Table(name = "audit_log",
    indexes = {
        @Index(name = "idx_user_action", columnList = "user_id, action"),
        @Index(name = "idx_created_at", columnList = "createdAt"),
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

    /**
     * ผู้ที่ทำการกระทำ (actor)
     * - Employee: login, check-in
     * - HR: view insight, assign dept
     * - Admin: add user, deactivate
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * การกระทำ - 14 ประเภท
     * แบ่งเป็น:
     * - Authentication: LOGIN, LOGOUT, REGISTER, LOGIN_FAILED
     * - Check-in: CHECK_IN, VIEW_DASHBOARD
     * - HR: VIEW_EMPLOYEE_INSIGHT, ASSIGN_DEPARTMENT, SEND_NOTIFICATION
     * - Admin: ADD_USER, EDIT_USER, DEACTIVATE_USER, ACTIVATE_USER, VIEW_AUDIT_LOG
     * - System: PASSWORD_CHANGE, PROFILE_UPDATE
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private Action action;

    /**
     * เป้าหมายของการกระทำ (target)
     * - ใช้เมื่อ action เกี่ยวข้องกับ user อื่น
     * 
     * ตัวอย่าง:
     * - HR assign department → target = employee
     * - Admin deactivate user → target = user that was deactivated
     * - HR view employee insight → target = employee
     * 
     * NULL ถ้า action ไม่เกี่ยวกับ user อื่น (เช่น login, check-in)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id")
    private User targetUser;

    /**
     * รายละเอียดเพิ่มเติม (JSON format)
     * 
     * ตัวอย่าง:
     * - LOGIN: {"browser": "Chrome", "os": "Windows"}
     * - CHECK_IN: {"emotionLevel": 1, "hasComment": true}
     * - ASSIGN_DEPARTMENT: {"department": "IT"}
     * - DEACTIVATE_USER: {"reason": "Resigned"}
     */
    @Column(columnDefinition = "TEXT")
    private String details;

    /**
     * IP Address ของผู้ใช้
     * - IPv4: "192.168.1.1"
     * - IPv6: "2001:0db8:85a3:0000:0000:8a2e:0370:7334"
     * 
     * ใช้ตรวจสอบ:
     * - การ login จาก IP แปลกๆ
     * - Multiple login attempts
     */
    @Column(length = 45)
    private String ipAddress;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ========== Enums ==========

    public enum Action {
        // Authentication
        LOGIN("เข้าสู่ระบบ"),
        LOGOUT("ออกจากระบบ"),
        REGISTER("ลงทะเบียน"),
        LOGIN_FAILED("เข้าสู่ระบบล้มเหลว"),

        // Check-in
        CHECK_IN("เช็คอินอารมณ์"),
        VIEW_DASHBOARD("ดู Dashboard"),

        // HR Actions
        VIEW_EMPLOYEE_INSIGHT("ดูข้อมูลพนักงาน"),
        ASSIGN_DEPARTMENT("กำหนดแผนก"),
        SEND_NOTIFICATION("ส่ง Notification"),

        // Admin Actions
        ADD_USER("เพิ่มผู้ใช้"),
        EDIT_USER("แก้ไขผู้ใช้"),
        DEACTIVATE_USER("ปิดการใช้งาน"),
        ACTIVATE_USER("เปิดการใช้งาน"),
        VIEW_AUDIT_LOG("ดู Audit Log"),

        // System Actions
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

    // ========== Helper Methods ==========

    /**
     * สร้าง details เป็น JSON format
     * ตัวอย่าง: createDetails("department", "IT") 
     * → {"department":"IT"}
     */
    public static String createDetails(String key, Object value) {
        return String.format("{\"%s\":\"%s\"}", key, value);
    }

    /**
     * เช็คว่าเป็น critical action หรือไม่
     * Critical = การกระทำสำคัญที่ต้องระวัง
     * - ADD_USER, DEACTIVATE_USER, ASSIGN_DEPARTMENT, PASSWORD_CHANGE
     */
    public boolean isCriticalAction() {
        return action == Action.ADD_USER ||
               action == Action.DEACTIVATE_USER ||
               action == Action.ASSIGN_DEPARTMENT ||
               action == Action.PASSWORD_CHANGE;
    }

    /**
     * เช็คว่าเป็น authentication action หรือไม่
     */
    public boolean isAuthAction() {
        return action == Action.LOGIN ||
               action == Action.LOGOUT ||
               action == Action.REGISTER ||
               action == Action.LOGIN_FAILED;
    }

    /**
     * เช็คว่า action นี้ต้องมี targetUser หรือไม่
     * true = ต้องระบุว่ากระทำกับใคร
     */
    public boolean requiresTargetUser() {
        return action == Action.VIEW_EMPLOYEE_INSIGHT ||
               action == Action.ASSIGN_DEPARTMENT ||
               action == Action.ADD_USER ||
               action == Action.EDIT_USER ||
               action == Action.DEACTIVATE_USER ||
               action == Action.ACTIVATE_USER ||
               action == Action.SEND_NOTIFICATION;
    }

    /**
     * Format action description พร้อมชื่อ target
     * ตัวอย่าง: "กำหนดแผนก (John Doe)"
     */
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