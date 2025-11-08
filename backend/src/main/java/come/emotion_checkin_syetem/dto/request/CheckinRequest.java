package come.emotion_checkin_syetem.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 📍 LOCATION: src/main/java/com/emotion/checkin/dto/request/CheckinRequest.java
 * 
 * 😊 CHECK-IN REQUEST - ข้อมูลสำหรับ check-in อารมณ์
 * 
 * ✅ Frontend ส่งมา:
 * {
 *   "emotionLevel": 3,
 *   "emotionTypeId": 5,
 *   "comment": "Feeling great today!"
 * }
 * 
 * ⚠️ Validations:
 * - emotionLevel: required, 1-3 only
 * - emotionTypeId: required (link to EmotionCatalog)
 * - comment: optional, max 1000 characters
 * 
 * 🔧 Business Logic:
 * - Check ว่า check-in วันนี้แล้วหรือยัง (1 ครั้ง/วัน)
 * - Auto-set checkinTime = now (Thailand timezone)
 * - ถ้ามี comment → ส่งไป Google NLP วิเคราะห์
 * - ถ้า emotionLevel = 1 → แจ้งเตือน HR
 * 
 * 🐛 DEBUG CHECKLIST:
 * ✅ Package: com.emotion.checkin.dto.request
 * ✅ @Data + Validation annotations
 * ✅ emotionLevel = 1, 2, 3 เท่านั้น
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckinRequest {
    
    @NotNull(message = "Emotion level is required")
    @Min(value = 1, message = "Emotion level must be 1 (Negative), 2 (Neutral), or 3 (Positive)")
    @Max(value = 3, message = "Emotion level must be 1 (Negative), 2 (Neutral), or 3 (Positive)")
    private Integer emotionLevel;
    
    @NotNull(message = "Emotion type is required")
    private Long emotionTypeId;  // Foreign key to EmotionCatalog
    
    @Size(max = 1000, message = "Comment must be less than 1000 characters")
    private String comment;  // Optional
}