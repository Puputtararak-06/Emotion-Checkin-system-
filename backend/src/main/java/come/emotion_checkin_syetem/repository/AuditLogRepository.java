package come.emotion_checkin_syetem.repository;

import come.emotion_checkin_syetem.entity.AuditLog;
import come.emotion_checkin_syetem.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 📍 LOCATION: src/main/java/come/emotion_checkin_syetem/repository/AuditLogRepository.java
 *
 * 📝 AUDIT LOG REPOSITORY - บันทึกการกระทำทั้งหมด
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * 📋 หา audit log ทั้งหมดเรียงตามวันที่ล่าสุด (with pagination)
     *
     * ✅ TESTED: Admin audit log page
     *
     * @param pageable Pageable (page number, size, sort)
     * @return Page<AuditLog>
     */
    Page<AuditLog> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * 👤 หา audit log ของ user
     *
     * ✅ TESTED: User activity history
     *
     * @param user User entity
     * @return List<AuditLog> - sorted by createdAt DESC
     */
    List<AuditLog> findByUserOrderByCreatedAtDesc(User user);

    /**
     * 🎯 หา audit log ตาม action
     *
     * ✅ TESTED: Filter by action type
     *
     * @param action AuditLog.Action enum
     * @return List<AuditLog>
     */
    List<AuditLog> findByActionOrderByCreatedAtDesc(AuditLog.Action action);

    /**
     * 👤🎯 หา audit log ของ user และ action
     *
     * ✅ TESTED: Specific user + specific action
     *
     * @param user User entity
     * @param action AuditLog.Action enum
     * @return List<AuditLog>
     */
    List<AuditLog> findByUserAndActionOrderByCreatedAtDesc(User user, AuditLog.Action action);

    /**
     * 🎯 หา audit log ที่เกี่ยวข้องกับ target user
     *
     * ✅ TESTED: Track actions on specific user
     *
     * @param targetUser User entity
     * @return List<AuditLog>
     */
    List<AuditLog> findByTargetUserOrderByCreatedAtDesc(User targetUser);

    /**
     * 📅 หา audit log ในช่วงเวลาที่กำหนด
     *
     * ✅ TESTED: Date range filter
     *
     * @param startDate วันเริ่มต้น
     * @param endDate วันสิ้นสุด
     * @return List<AuditLog>
     */
    @Query("SELECT al FROM AuditLog al " +
           "WHERE al.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY al.createdAt DESC")
    List<AuditLog> findByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * 🔍 ค้นหา audit log จากชื่อ user หรือ target user
     *
     * ✅ TESTED: Search bar - search by name
     *
     * @param keyword คำค้นหา
     * @return List<AuditLog>
     */
    @Query("SELECT al FROM AuditLog al " +
           "WHERE LOWER(al.user.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(al.targetUser.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "ORDER BY al.createdAt DESC")
    List<AuditLog> searchByUserName(@Param("keyword") String keyword);

    /**
     * 🔍 ค้นหา audit log แบบครอบคลุม (role + action + keyword)
     *
     * ✅ TESTED: Advanced search with multiple filters
     *
     * @param role User.Role enum (optional - null = all roles)
     * @param action AuditLog.Action enum (optional - null = all actions)
     * @param keyword Search keyword (optional - null = no keyword filter)
     * @param pageable Pageable
     * @return Page<AuditLog>
     */
    @Query("SELECT al FROM AuditLog al " +
           "WHERE (:role IS NULL OR al.user.role = :role) " +
           "AND (:action IS NULL OR al.action = :action) " +
           "AND (:keyword IS NULL OR " +
           "     LOWER(al.user.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "     LOWER(al.targetUser.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY al.createdAt DESC")
    Page<AuditLog> advancedSearch(
        @Param("role") User.Role role,
        @Param("action") AuditLog.Action action,
        @Param("keyword") String keyword,
        Pageable pageable
    );

    /**
     * 🚨 หา critical actions ล่าสุด
     *
     * ✅ TESTED: Admin dashboard - critical activities
     *
     * @return List<AuditLog>
     */
    @Query("SELECT al FROM AuditLog al " +
           "WHERE al.action IN ('ADD_USER', 'DEACTIVATE_USER', 'ASSIGN_DEPARTMENT', 'PASSWORD_CHANGE') " +
           "ORDER BY al.createdAt DESC")
    List<AuditLog> findCriticalActions();

    /**
     * 📊 นับจำนวน audit log แต่ละ action
     *
     * ✅ TESTED: Admin statistics
     *
     * @return List<Object[]> - [action, count]
     */
    @Query("SELECT al.action, COUNT(al) FROM AuditLog al " +
           "GROUP BY al.action")
    List<Object[]> countByAction();

    /**
     * 🔍 หา failed login attempts
     *
     * ✅ TESTED: Security monitoring
     *
     * @param user User entity
     * @param startDate วันเริ่มต้น
     * @return List<AuditLog>
     */
    @Query("SELECT al FROM AuditLog al " +
           "WHERE al.user = :user " +
           "AND al.action = 'LOGIN_FAILED' " +
           "AND al.createdAt >= :startDate " +
           "ORDER BY al.createdAt DESC")
    List<AuditLog> findFailedLoginAttempts(
        @Param("user") User user,
        @Param("startDate") LocalDateTime startDate
    );
}
