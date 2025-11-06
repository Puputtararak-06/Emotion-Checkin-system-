package come.emotion_checkin_syetem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 📍 LOCATION: src/main/java/com/emotion/checkin/dto/response/DepartmentStatsDTO.java
 * 
 * 🏢 DEPARTMENT STATISTICS DTO - สถิติแผนก (HR Dashboard)
 * 
 * ✅ ใช้ที่: HR Dashboard - แสดงสถิติแต่ละแผนก
 * 
 * ⚠️ PRIVACY:
 * - ไม่มี comment!
 * - แสดงเฉพาะ aggregated data
 * 
 * 📊 Frontend จะได้:
 * {
 *   "department": "IT",
 *   "totalEmployees": 25,
 *   "activeEmployees": 23,
 *   "checkinRate": 92.0,
 *   "todayCheckins": 20,
 *   "positiveCount": 15,
 *   "neutralCount": 4,
 *   "negativeCount": 1,
 *   "averageMoodScore": 2.7,
 *   "highRiskCount": 0
 * }
 * 
 * 🐛 DEBUG CHECKLIST:
 * ✅ Package: com.emotion.checkin.dto.response
 * ✅ @Data + @Builder
 * ✅ Aggregated data only
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentStats {
    
    private String department;
    
    // Employee counts
    private Long totalEmployees;
    private Long activeEmployees;
    private Long inactiveEmployees;
    
    // Check-in rate
    private Double checkinRate;         // 92.0 = 92%
    private Long todayCheckins;
    private Long weeklyCheckins;
    private Long monthlyCheckins;
    
    // Mood distribution
    private Long positiveCount;         // Level 3
    private Long neutralCount;          // Level 2
    private Long negativeCount;         // Level 1
    
    // Average mood (1.0 to 3.0)
    private Double averageMoodScore;    // 2.7 = mostly positive
    
    // Alerts
    private Integer highRiskCount;      // พนักงานที่มีความเสี่ยงสูง
    private Integer consecutiveBadMoodCount; // พนักงานที่อารมณ์แย่ติดต่อกัน
    
    // Trend
    private String moodTrend;           // "IMPROVING", "DECLINING", "STABLE"
    private Double trendPercentage;     // +10.5 หรือ -5.3
}