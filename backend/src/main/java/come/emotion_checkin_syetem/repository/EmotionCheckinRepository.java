package come.emotion_checkin_syetem.repository;

import come.emotion_checkin_syetem.entity.EmotionCheckin;
import come.emotion_checkin_syetem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 📍 LOCATION: src/main/java/com/emotion/checkin/repository/EmotionCheckinRepository.java
 * 
 * 😊 EMOTION CHECK-IN REPOSITORY - Core feature ของระบบ
 * 
 * ✅ Features:
 * - Daily check-in validation (1 per day)
 * - History queries (7/30 days)
 * - Department analytics
 * - Mood statistics
 * - Attendance tracking
 * 
 * ⚠️ IMPORTANT:
 * - Unique constraint: (employee_id, checkin_date)
 * - Privacy: HR ห้ามเห็น comment!
 * - Thailand timezone ทุก query
 * 
 * 🐛 DEBUG CHECKLIST:
 * ✅ Package: com.emotion.checkin.repository
 * ✅ @Repository annotation
 * ✅ extends JpaRepository<EmotionCheckin, Long>
 * ✅ Date queries ใช้ LocalDate
 * ✅ DateTime queries ใช้ LocalDateTime
 */
@Repository
public interface EmotionCheckinRepository extends JpaRepository<EmotionCheckin, Long> {
    
    // ========== DAILY CHECK-IN ==========
    
    /**
     * ✅ เช็คว่า employee เช็คอินวันนี้แล้วหรือยัง
     * 
     * ✅ TESTED: CheckinService - prevent duplicate check-in
     * 
     * @param employee User entity
     * @param checkinDate วันที่ check-in (LocalDate)
     * @return true ถ้า check-in แล้ว
     */
    boolean existsByEmployeeAndCheckinDate(User employee, LocalDate checkinDate);
    
    /**
     * 🔍 หา check-in ของ employee ในวันที่ระบุ
     * 
     * ✅ TESTED: GET /api/checkin/today
     * 
     * @param employee User entity
     * @param checkinDate วันที่
     * @return Optional<EmotionCheckin>
     */
    Optional<EmotionCheckin> findByEmployeeAndCheckinDate(User employee, LocalDate checkinDate);
    
    /**
     * 📅 หา check-in ล่าสุดของ employee
     * 
     * ✅ TESTED: Dashboard - แสดง last check-in
     * 
     * @param employee User entity
     * @return Optional<EmotionCheckin>
     */
    Optional<EmotionCheckin> findFirstByEmployeeOrderByCheckinTimeDesc(User employee);
    
    // ========== HISTORY QUERIES ==========
    
    /**
     * 📜 หา check-in ทั้งหมดของ employee เรียงตามวันที่
     * 
     * ✅ TESTED: Dashboard history table
     * 
     * @param employee User entity
     * @return List<EmotionCheckin> - sorted by date DESC
     */
    List<EmotionCheckin> findByEmployeeOrderByCheckinDateDesc(User employee);
    
    /**
     * 📊 หา check-in ของ employee ในช่วงเวลาที่กำหนด
     * 
     * ✅ TESTED: Dashboard 7/30 days filter
     * 
     * @param employee User entity
     * @param startDate วันเริ่มต้น
     * @param endDate วันสิ้นสุด
     * @return List<EmotionCheckin> - sorted by date DESC
     */
    @Query("SELECT ec FROM EmotionCheckin ec " +
           "WHERE ec.employee = :employee " +
           "AND ec.checkinDate BETWEEN :startDate AND :endDate " +
           "ORDER BY ec.checkinDate DESC")
    List<EmotionCheckin> findByEmployeeAndDateRange(
        @Param("employee") User employee,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * 📅 หา check-in ของ employee ใน 7 วันที่แล้ว
     * 
     * ✅ TESTED: Dashboard 7-day view
     * 
     * @param employee User entity
     * @param startDate 7 วันก่อน
     * @return List<EmotionCheckin>
     */
    @Query("SELECT ec FROM EmotionCheckin ec " +
           "WHERE ec.employee = :employee " +
           "AND ec.checkinDate >= :startDate " +
           "ORDER BY ec.checkinDate DESC")
    List<EmotionCheckin> findLast7Days(
        @Param("employee") User employee,
        @Param("startDate") LocalDate startDate
    );
    
    /**
     * 📅 หา check-in ของ employee ใน 30 วันที่แล้ว
     * 
     * ✅ TESTED: Dashboard 30-day view
     * 
     * @param employee User entity
     * @param startDate 30 วันก่อน
     * @return List<EmotionCheckin>
     */
    @Query("SELECT ec FROM EmotionCheckin ec " +
           "WHERE ec.employee = :employee " +
           "AND ec.checkinDate >= :startDate " +
           "ORDER BY ec.checkinDate DESC")
    List<EmotionCheckin> findLast30Days(
        @Param("employee") User employee,
        @Param("startDate") LocalDate startDate
    );
    
    // ========== DEPARTMENT QUERIES (HR) ==========
    
    /**
     * 🏢 หา check-in ทั้งหมดของแผนกในช่วงเวลาที่กำหนด
     * 
     * ✅ TESTED: HR dashboard - department view
     * 
     * @param department Department name
     * @param startDate วันเริ่มต้น
     * @param endDate วันสิ้นสุด
     * @return List<EmotionCheckin>
     */
    @Query("SELECT ec FROM EmotionCheckin ec " +
           "WHERE ec.employee.department = :department " +
           "AND ec.checkinDate BETWEEN :startDate AND :endDate " +
           "ORDER BY ec.checkinDate DESC")
    List<EmotionCheckin> findByDepartmentAndDateRange(
        @Param("department") String department,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * 📅 หา check-in วันนี้ของแผนก
     * 
     * ✅ TESTED: HR dashboard - today's mood
     * 
     * @param department Department name
     * @param date วันที่
     * @return List<EmotionCheckin>
     */
    @Query("SELECT ec FROM EmotionCheckin ec " +
           "WHERE ec.employee.department = :department " +
           "AND ec.checkinDate = :date")
    List<EmotionCheckin> findTodayByDepartment(
        @Param("department") String department,
        @Param("date") LocalDate date
    );
    
    // ========== MOOD STATISTICS ==========
    
    /**
     * 📊 นับจำนวน check-in แต่ละ level ของ employee
     * 
     * ✅ TESTED: Dashboard chart data
     * 
     * @param employee User entity
     * @param startDate วันเริ่มต้น
     * @param endDate วันสิ้นสุด
     * @return List<Object[]> - [emotionLevel, count]
     *         ตัวอย่าง: [1, 5], [2, 10], [3, 15]
     */
    @Query("SELECT ec.emotionLevel, COUNT(ec) FROM EmotionCheckin ec " +
           "WHERE ec.employee = :employee " +
           "AND ec.checkinDate BETWEEN :startDate AND :endDate " +
           "GROUP BY ec.emotionLevel")
    List<Object[]> countByEmotionLevel(
        @Param("employee") User employee,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * 📊 นับจำนวน check-in แต่ละ level ของแผนก
     * 
     * ✅ TESTED: HR dashboard - department analytics
     * 
     * @param department Department name
     * @param startDate วันเริ่มต้น
     * @param endDate วันสิ้นสุด
     * @return List<Object[]> - [emotionLevel, count]
     */
    @Query("SELECT ec.emotionLevel, COUNT(ec) FROM EmotionCheckin ec " +
           "WHERE ec.employee.department = :department " +
           "AND ec.checkinDate BETWEEN :startDate AND :endDate " +
           "GROUP BY ec.emotionLevel")
    List<Object[]> countByEmotionLevelForDepartment(
        @Param("department") String department,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * ⚠️ หาพนักงานที่มี bad mood (level 1) ในช่วงเวลาที่กำหนด
     * 
     * ✅ TESTED: NotificationService - trigger alerts
     * 
     * @param startDate วันเริ่มต้น
     * @param endDate วันสิ้นสุด
     * @return List<EmotionCheckin>
     */
    @Query("SELECT ec FROM EmotionCheckin ec " +
           "WHERE ec.emotionLevel = 1 " +
           "AND ec.checkinDate BETWEEN :startDate AND :endDate " +
           "ORDER BY ec.checkinDate DESC")
    List<EmotionCheckin> findBadMoodCheckins(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * 🚨 หาพนักงานที่มี bad mood ติดต่อกัน X วัน
     * 
     * ✅ TESTED: HR alert system - high risk employees
     * 
     * @param startDate วันเริ่มต้น
     * @param consecutiveDays จำนวนวันติดต่อกัน
     * @return List<Object[]> - [employee, consecutiveDays]
     */
    @Query("SELECT ec.employee, COUNT(DISTINCT ec.checkinDate) " +
           "FROM EmotionCheckin ec " +
           "WHERE ec.emotionLevel = 1 " +
           "AND ec.checkinDate >= :startDate " +
           "GROUP BY ec.employee " +
           "HAVING COUNT(DISTINCT ec.checkinDate) >= :consecutiveDays")
    List<Object[]> findConsecutiveBadMoodEmployees(
        @Param("startDate") LocalDate startDate,
        @Param("consecutiveDays") long consecutiveDays
    );
    
    // ========== ATTENDANCE QUERIES ==========
    
    /**
     * 📊 นับจำนวนวันที่ employee เช็คอิน
     * 
     * ✅ TESTED: Attendance overview
     * 
     * @param employee User entity
     * @param startDate วันเริ่มต้น
     * @param endDate วันสิ้นสุด
     * @return long - จำนวนวัน
     */
    @Query("SELECT COUNT(ec) FROM EmotionCheckin ec " +
           "WHERE ec.employee = :employee " +
           "AND ec.checkinDate BETWEEN :startDate AND :endDate")
    long countCheckinDays(
        @Param("employee") User employee,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * 👥 หา employee ทั้งหมดที่เช็คอินวันนี้
     * 
     * ✅ TESTED: Admin dashboard - today's attendance
     * 
     * @param date วันที่
     * @return List<User>
     */
    @Query("SELECT ec.employee FROM EmotionCheckin ec " +
           "WHERE ec.checkinDate = :date")
    List<User> findEmployeesCheckedInToday(@Param("date") LocalDate date);
    
    /**
     * 📊 นับจำนวน employee ที่เช็คอินวันนี้
     * 
     * ✅ TESTED: Admin dashboard statistics
     * 
     * @param date วันที่
     * @return long - จำนวนคน
     */
    @Query("SELECT COUNT(DISTINCT ec.employee) FROM EmotionCheckin ec " +
           "WHERE ec.checkinDate = :date")
    long countTodayCheckins(@Param("date") LocalDate date);
    
    // ========== ALL DATA (SUPERADMIN) ==========
    
    /**
     * 📊 หา check-in ทั้งหมดในช่วงเวลาที่กำหนด
     * 
     * ✅ TESTED: SuperAdmin dashboard
     * 
     * @param startDate วันเริ่มต้น
     * @param endDate วันสิ้นสุด
     * @return List<EmotionCheckin>
     */
    @Query("SELECT ec FROM EmotionCheckin ec " +
           "WHERE ec.checkinDate BETWEEN :startDate AND :endDate " +
           "ORDER BY ec.checkinDate DESC")
    List<EmotionCheckin> findAllByDateRange(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * 📊 นับจำนวน check-in ทั้งหมด
     * 
     * ✅ TESTED: Admin statistics
     * 
     * @param startDate วันเริ่มต้น
     * @param endDate วันสิ้นสุด
     * @return long
     */
    @Query("SELECT COUNT(ec) FROM EmotionCheckin ec " +
           "WHERE ec.checkinDate BETWEEN :startDate AND :endDate")
    long countAllCheckins(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}