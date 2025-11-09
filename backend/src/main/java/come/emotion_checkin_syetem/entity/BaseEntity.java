package come.emotion_checkin_syetem.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 🏗️ BASE ENTITY - คลาสแม่สำหรับทุก Entity
 * 
 * ❓ ทำไมต้องมี?
 * 1. Audit Trail - รู้ว่าข้อมูลถูกสร้าง/แก้ไขเมื่อไหร่
 * 2. Soft Delete - ลบโดยไม่ลบจริง (ตาม PDPA)
 * 3. Thailand Timezone - บันทึกเวลาตาม timezone ไทย
 * 4. Code ไม่ซ้ำ - เขียนครั้งเดียว ใช้ได้ทุก Entity
 * 
 * 📊 ER Diagram Impact:
 * ทุก table จะมี columns เพิ่ม:
 * - created_at TIMESTAMP
 * - updated_at TIMESTAMP  
 * - deleted_at TIMESTAMP
 * 
 * 🔧 Usage:
 * public class User extends BaseEntity { ... }
 * 
 * ⚠️ @MappedSuperclass = ไม่สร้าง table แยก!
 */
@MappedSuperclass  // ⭐ บอก JPA ว่านี่คือ parent class ไม่สร้าง table
@Getter
@Setter
@Where(clause = "deleted_at IS NULL")  // ⭐ Auto-filter: ไม่แสดงข้อมูลที่ถูกลบ
public abstract class BaseEntity {

    /**
     * วันเวลาที่สร้างข้อมูล (Thailand timezone)
     * - Auto-fill ตอน insert
     * - ห้ามแก้ไข (updatable = false)
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * วันเวลาที่แก้ไขข้อมูลล่าสุด (Thailand timezone)
     * - Auto-update ตอน update
     * - NULL ถ้ายังไม่เคยแก้ไข
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * วันเวลาที่ลบข้อมูล (Soft Delete)
     * - NULL = ยังไม่ถูกลบ
     * - มีค่า = ถูกลบแล้ว (แต่ยังอยู่ใน database)
     * 
     * ✅ ประโยชน์:
     * - กู้คืนข้อมูลได้
     * - เก็บประวัติไว้ตาม PDPA
     * - Audit trail สมบูรณ์
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * 🔧 ทำงานก่อน INSERT (save ครั้งแรก)
     * ตั้งค่า createdAt = เวลาปัจจุบัน (Thailand timezone)
     */
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now(ZoneId.of("Asia/Bangkok"));
        }
    }

    /**
     * 🔧 ทำงานก่อน UPDATE (save ครั้งที่ 2 เป็นต้นไป)
     * ตั้งค่า updatedAt = เวลาปัจจุบัน (Thailand timezone)
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now(ZoneId.of("Asia/Bangkok"));
    }

    /**
     * 🗑️ Soft Delete - ลบโดยไม่ลบจริง
     * ตั้งค่า deletedAt แทนการ DELETE จาก database
     * 
     * ✅ ใช้งาน:
     * user.softDelete();
     * userRepository.save(user);
     */
    public void softDelete() {
        this.deletedAt = LocalDateTime.now(ZoneId.of("Asia/Bangkok"));
    }

    /**
     * 🔄 Restore - กู้คืนข้อมูลที่ถูก soft delete
     * 
     * ✅ ใช้งาน:
     * user.restore();
     * userRepository.save(user);
     */
    public void restore() {
        this.deletedAt = null;
    }

    /**
     * ✅ เช็คว่าถูกลบหรือยัง
     * @return true ถ้าถูก soft delete แล้ว
     */
    public boolean isDeleted() {
        return deletedAt != null;
    }

    /**
     * ✅ เช็คว่ายัง active อยู่หรือไม่
     * @return true ถ้ายังไม่ถูกลบ
     */
    public boolean isActive() {
        return deletedAt == null;
    }
}