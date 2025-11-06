package come.emotion_checkin_syetem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 📍 LOCATION: src/main/java/com/emotion/checkin/dto/response/CheckinResponse.java
 * 
 * 😊 CHECK-IN RESPONSE - ผลลัพธ์หลัง check-in
 * 
 * ✅ Frontend จะได้:
 * {
 *   "checkinId": 123,
 *   "emoji": "😊",
 *   "mood": "Happy",
 *   "emotionLevel": 3,
 *   "checkinTime": "2025-11-07T14:30:00",
 *   "note": "Feeling great today!",
 *   "sentimentScore": 0.85,
 *   "sentimentLabel": "POSITIVE"
 * }
 * 
 * 🐛 DEBUG CHECKLIST:
 * ✅ Package: com.emotion.checkin.dto.response
 * ✅ @Data + @Builder
 * ✅ LocalDateTime format
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckinResponse {
    
    private Long checkinId;
    private String emoji;           // "😊", "😐", "😢"
    private String mood;            // "Happy", "Calm", "Sad"
    private Integer emotionLevel;   // 1, 2, 3
    private LocalDateTime checkinTime;
    private String note;            // Employee's comment
    
    // AI Analysis (optional - อาจจะ null)
    private Float sentimentScore;   // -1.0 to 1.0
    private String sentimentLabel;  // "POSITIVE", "NEUTRAL", "NEGATIVE"
}