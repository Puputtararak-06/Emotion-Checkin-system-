package come.emotion_checkin_syetem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 📍 LOCATION: src/main/java/com/emotion/checkin/dto/response/CheckinHistoryDTO.java
 * 
 * 📜 CHECK-IN HISTORY DTO - รายการ check-in ย้อนหลัง
 * 
 * ✅ ใช้ที่: Dashboard history table
 * 
 * ⚠️ PRIVACY:
 * - Employee: เห็น comment ของตัวเอง
 * - HR: ไม่เห็น comment! (null)
 * - SuperAdmin: เห็น comment ทั้งหมด
 * 
 * 📊 Frontend จะได้:
 * {
 *   "id": 123,
 *   "date": "2025-11-07",
 *   "emoji": "😊",
 *   "mood": "Happy",
 *   "level": 3,
 *   "hasComment": true,
 *   "comment": "Feeling great!",  // null สำหรับ HR
 *   "sentimentScore": 0.85
 * }
 * 
 * 🐛 DEBUG CHECKLIST:
 * ✅ Package: com.emotion.checkin.dto.response
 * ✅ @Data + @Builder
 * ✅ comment อาจเป็น null (HR)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckinHistory {
    
    private Long id;
    private LocalDate date;
    private String emoji;           // "😊", "😐", "😢"
    private String mood;            // "Happy", "Calm", "Sad"
    private Integer level;          // 1, 2, 3
    private String colorCode;       // "#4CAF50", "#FFC107", "#F44336"
    
    // Comment (Privacy-aware!)
    private Boolean hasComment;     // true/false (ใช้แสดง icon)
    private String comment;         // null สำหรับ HR!
    
    // AI Analysis
    private Float sentimentScore;   // -1.0 to 1.0
    private String sentimentLabel;  // "POSITIVE", "NEUTRAL", "NEGATIVE"
    
    // Timestamps
    private LocalDateTime checkinTime;
    private String timeAgo;         // "5 minutes ago", "2 days ago"
}