package com.emotion.checkin.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 🧑 User Entity - รวม Employee, HR, SuperAdmin เป็น table เดียว
 * 
 * ทำไมรวม?
 * - ตามคำแนะนำอาจารย์: ใช้ Role แยกแทนการสร้าง 3 tables
 * - ง่ายต่อการ query และ maintain
 * - ใช้ Enum Role เป็นตัวแยกประเภท
 * 
 * Fields สำคัญ:
 * - isActive: Soft delete flag (ไม่ลบจริง)
 * - department: NULL สำหรับ HR/SuperAdmin
 * - role: EMPLOYEE | HR | SUPERADMIN
 */
@Entity
@Table(name = "user", indexes = {
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_role", columnList = "role"),
    @Index(name = "idx_department", columnList = "department"),
    @Index(name = "idx_active", columnList = "isActive")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name; // ชื่อเต็ม (firstName + lastName)

    @Column(nullable = false, unique = true, length = 255)
    private String email; // สำหรับ login

    @Column(nullable = false, length = 255)
    private String password; // BCrypt hashed (ไม่เก็บ plain text!)

    @Column(length = 100)
    private String department; // IT, Business, Finance (NULL ถ้าเป็น HR/Admin)

    @Column(length = 100)
    private String position; // ตำแหน่งงาน (NULL ถ้าเป็น HR/Admin)

    /**
     * Role Enum - แยกประเภทผู้ใช้
     * - EMPLOYEE: พนักงานทั่วไป (check-in อารมณ์)
     * - HR: ดูข้อมูลพนักงาน แต่ห้ามเห็น raw comments!
     * - SUPERADMIN: เห็นทุกอย่าง รวมถึง comments
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    /**
     * Soft Delete Flag
     * - true: Active (ใช้งานได้)
     * - false: Deactivated (ถูกระงับ)
     * ไม่ลบข้อมูลออกจาก database เพื่อเก็บประวัติ
     */
    @Column(nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // วันที่สร้าง account

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt; // วันที่แก้ไขล่าสุด

    // ========== Relationships (1 User มีได้หลาย Records) ==========

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmotionCheckin> checkins = new ArrayList<>();

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    private List<Notification> sentNotifications = new ArrayList<>();

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    private List<Notification> receivedNotifications = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<AuditLog> auditLogs = new ArrayList<>();

    // ========== Enums ==========

    public enum Role {
        EMPLOYEE,   // พนักงาน
        HR,         // HR
        SUPERADMIN  // ผู้ดูแลระบบ
    }

    // ========== Helper Methods (ใช้ตรวจสอบ Role) ==========

    /**
     * เช็คว่าเป็น Employee หรือไม่
     */
    public boolean isEmployee() {
        return role == Role.EMPLOYEE;
    }

    /**
     * เช็คว่าเป็น HR หรือไม่
     */
    public boolean isHR() {
        return role == Role.HR;
    }

    /**
     * เช็คว่าเป็น SuperAdmin หรือไม่
     */
    public boolean isSuperAdmin() {
        return role == Role.SUPERADMIN;
    }

    /**
     * เช็คว่าสามารถดู raw comments ได้หรือไม่
     * - SUPERADMIN: ดูได้
     * - HR: ห้ามดู!
     * - EMPLOYEE: ดูของตัวเองได้
     */
    public boolean canViewRawComments() {
        return role == Role.SUPERADMIN;
    }

    /**
     * เช็คว่าสามารถ assign department ได้หรือไม่
     */
    public boolean canAssignDepartment() {
        return role == Role.HR || role == Role.SUPERADMIN;
    }

    /**
     * Soft delete - ระงับการใช้งาน
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * เปิดใช้งานอีกครั้ง
     */
    public void activate() {
        this.isActive = true;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", department='" + department + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}