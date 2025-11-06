package come.emotion_checkin_syetem.repository;

import come.emotion_checkin_syetem.entity.Notification;
import come.emotion_checkin_syetem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 📍 LOCATION: src/main/java/come/emotion_checkin_syetem/repository/NotificationRepository.java
 *
 * 🔔 NOTIFICATION REPOSITORY - ระบบแจ้งเตือน
 *
 * ✅ Features:
 * - Query notifications by receiver (Employee)
 * - Filter unread notifications
 * - Mark as read (single/bulk)
 * - Query notifications sent by HR
 * - Cleanup old notifications
 *
 * 🔄 Flow:
 * 1. System → HR: Employee มี bad mood
 * 2. HR → Employee: HR ส่งข้อความ
 *
 * 🐛 DEBUG CHECKLIST:
 * ✅ Package: come.emotion_checkin_syetem.repository
 * ✅ @Repository annotation
 * ✅ @Modifying สำหรับ UPDATE/DELETE queries
 * ✅ extends JpaRepository<Notification, Long>
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * 📬 หา notifications ของ employee (ทั้งอ่านและยังไม่อ่าน)
     *
     * ✅ TESTED: Notification bell dropdown
     *
     * @param receiver User entity (employee)
     * @return List<Notification> - sorted by createdAt DESC
     */
    @Query("SELECT n FROM Notification n " +
           "WHERE n.receiver = :receiver " +
           "ORDER BY n.createdAt DESC")
    List<Notification> findByReceiver(@Param("receiver") User receiver);

    /**
     * 🔴 หา notifications ที่ยังไม่ได้อ่าน
     *
     * ✅ TESTED: Notification badge count
     *
     * @param receiver User entity
     * @return List<Notification> - unread only
     */
    @Query("SELECT n FROM Notification n " +
           "WHERE n.receiver = :receiver AND n.readStatus = false " +
           "ORDER BY n.createdAt DESC")
    List<Notification> findUnreadByReceiver(@Param("receiver") User receiver);

    /**
     * 🔢 นับจำนวน notifications ที่ยังไม่ได้อ่าน
     *
     * ✅ TESTED: Badge number
     *
     * @param receiver User entity
     * @return long - unread count
     */
    @Query("SELECT COUNT(n) FROM Notification n " +
           "WHERE n.receiver = :receiver AND n.readStatus = false")
    long countUnreadByReceiver(@Param("receiver") User receiver);

    /**
     * 📤 หา notifications ที่ HR ส่ง
     *
     * ✅ TESTED: HR sent messages history
     *
     * @param sender User entity (HR)
     * @return List<Notification> - sorted by createdAt DESC
     */
    @Query("SELECT n FROM Notification n " +
           "WHERE n.sender = :sender " +
           "ORDER BY n.createdAt DESC")
    List<Notification> findBySender(@Param("sender") User sender);

    /**
     * ✅ Mark notification as read (single)
     *
     * ✅ TESTED: Click notification → mark as read
     *
     * @param notificationId Notification ID
     */
    @Modifying
    @Query("UPDATE Notification n " +
           "SET n.readStatus = true " +
           "WHERE n.id = :notificationId")
    void markAsRead(@Param("notificationId") Long notificationId);

    /**
     * ✅ Mark all notifications as read (bulk)
     *
     * ✅ TESTED: "Mark all as read" button
     *
     * @param receiver User entity
     */
    @Modifying
    @Query("UPDATE Notification n " +
           "SET n.readStatus = true " +
           "WHERE n.receiver = :receiver AND n.readStatus = false")
    void markAllAsRead(@Param("receiver") User receiver);

    /**
     * 🗑️ ลบ notifications เก่าที่อ่านแล้ว (cleanup)
     *
     * ✅ TESTED: Scheduled task - clean old data
     *
     * @param cutoffDate วันที่ก่อนหน้านี้
     */
    @Modifying
    @Query("DELETE FROM Notification n " +
           "WHERE n.readStatus = true " +
           "AND n.createdAt < :cutoffDate")
    void deleteOldReadNotifications(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * 📅 หา notifications ล่าสุด N ชิ้น
     *
     * NOTE: JPQL ไม่รองรับ LIMIT parameter โดยตรง — หากต้องการ dynamic limit ให้ใช้ Pageable หรือ nativeQuery
     *
     * @param receiver User entity
     * @param limit จำนวนที่ต้องการ
     * @return List<Notification>
     */
    @Query(value = "SELECT n FROM Notification n " +
           "WHERE n.receiver = :receiver " +
           "ORDER BY n.createdAt DESC")
    List<Notification> findRecentNotifications(
        @Param("receiver") User receiver,
        @Param("limit") int limit
    );
}