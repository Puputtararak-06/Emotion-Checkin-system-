package come.emotion_checkin_syetem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 📍 LOCATION: src/main/java/com/emotion/checkin/dto/response/EmployeeInsightDTO.java
 * 
 * 👤 EMPLOYEE INSIGHT DTO - ข้อมูล employee แต่ละคน (HR/Admin view)
 * 
 * ✅ ใช้ที่:
 * - HR Dashboard - employee list
 * - Admin Dashboard - employee management
 * 
 * ⚠️ PRIVACY:
 * - HR: เห็น stats แต่ไม่เห็น comment!
 * - SuperAdmin: เห็นทุกอย่าง รวม comment
 * 
 * 📊 Frontend จะได้:
 * {
 *   "employeeId": 123,
 *   "name": "John Doe",
 *   "email": "john@example.com",
 *   "department": "IT",
 *   "position": "Developer",
 *   "lastCheckin": "2025-11-07",
 *   "lastMood": "Happy",
 *   "lastMoodLevel": 3,
 *   "checkinStreak": 5,
 *   "weeklyPositive": 4,
 *   "weeklyNeutral": 2,
 *   "weeklyNegative": 1,
 *   "isHighRisk": false,
 *   "averageSentiment": 0.65,
 *   "recentComment": null  // HR ไม่เห็น!
 * }
 * 
 * 🐛 DEBUG CHECKLIST:
 * ✅ Package: com.emotion.checkin.dto.response
 * ✅ @Data + @Builder
 * ✅ recentComment = null สำหรับ HR
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeInsight {
    
    // Employee info
    private Long employeeId;
    private String name;
    private String email;
    private String department;
    private String position;
    private Boolean isActive;
    
    // Last check-in
    private LocalDate lastCheckin;
    private String lastMood;        // "Happy", "Calm", "Sad"
    private Integer lastMoodLevel;  // 1, 2, 3
    private String lastMoodEmoji;   // "😊", "😐", "😢"
    
    // Streak
    private Integer checkinStreak;  // จำนวนวันติดต่อกันที่ check-in
    private Double checkinRate;     // 85.7% (last 30 days)
    
    // Weekly stats (7 days)
    private Long weeklyPositive;
    private Long weeklyNeutral;
    private Long weeklyNegative;
    
    // Monthly stats (30 days)
    private Long monthlyPositive;
    private Long monthlyNeutral;
    private Long monthlyNegative;
    
    // AI Analysis
    private Double averageSentiment;    // -1.0 to 1.0
    
    // Risk indicators
    private Boolean isHighRisk;         // very negative + high magnitude
    private Integer consecutiveBadDays; // จำนวนวันติดต่อกันที่อารมณ์แย่
    
    // ========== SUPERADMIN ONLY ==========
    
    // Comments (NULL สำหรับ HR!)
    private String recentComment;       // comment ล่าสุด (SuperAdmin only)
    private Boolean hasComment;         // true/false (ใช้แสดง icon)
}