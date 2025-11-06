package come.emotion_checkin_syetem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 📍 LOCATION: src/main/java/com/emotion/checkin/dto/response/DashboardResponse.java
 * 
 * 📊 DASHBOARD RESPONSE - ข้อมูล Dashboard ทั้งหมด
 * 
 * ✅ ใช้สำหรับ:
 * - Employee Dashboard: ดูข้อมูลของตัวเอง
 * - HR Dashboard: ดูข้อมูลแผนก (ไม่มี comment!)
 * - Admin Dashboard: ดูทุกอย่าง
 * 
 * 🔄 Dynamic content based on role:
 * - EMPLOYEE: myStats, recentCheckins (7 days)
 * - HR: departmentStats, employeeList
 * - SUPERADMIN: allStats, allDepartments
 * 
 * 📊 Frontend จะได้:
 * {
 *   "userName": "John Doe",
 *   "userRole": "EMPLOYEE",
 *   "stats": {...},
 *   "recentCheckins": [...],
 *   "checkinStreak": 5,
 *   "lastCheckinDate": "2025-11-07",
 *   "canCheckinToday": false
 * }
 * 
 * 🐛 DEBUG CHECKLIST:
 * ✅ Package: com.emotion.checkin.dto.response
 * ✅ @Data + @Builder
 * ✅ Flexible structure (different roles)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {
    
    // User info
    private String userName;
    private String userRole;        // "EMPLOYEE", "HR", "SUPERADMIN"
    private String department;
    
    // Statistics
    private EmotionStats stats;  // ใช้ DTO ที่เขียนไว้แล้ว
    
    // Recent check-ins (7 days)
    private List<CheckinHistory> recentCheckins;
    
    // Streak (จำนวนวันติดต่อกันที่ check-in)
    private Integer checkinStreak;
    private String lastCheckinDate;
    private Boolean canCheckinToday;
    
    // Notifications (unread count)
    private Long unreadNotifications;
    
    // ========== HR/ADMIN ONLY ==========
    
    // Department list (HR/Admin)
    private List<String> departments;
    
    // Department stats (HR)
    private List<DepartmentStats> departmentStats;
    
    // Employee insights (HR/Admin)
    private List<EmployeeInsight> employeeInsights;
    
    // Critical alerts (Admin)
    private Integer highRiskEmployees;
    private Integer consecutiveBadMoodCount;
}