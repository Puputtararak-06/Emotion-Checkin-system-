package come.emotion_checkin_syetem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 📍 LOCATION: src/main/java/com/emotion/checkin/dto/response/EmotionStatsDTO.java
 * 
 * 📊 EMOTION STATISTICS DTO - สถิติอารมณ์สำหรับ chart
 * 
 * ✅ ใช้ที่:
 * - Dashboard pie chart
 * - Department analytics
 * - Admin overview
 * 
 * 📊 Frontend จะได้:
 * {
 *   "totalCheckins": 30,
 *   "positiveCount": 18,
 *   "neutralCount": 8,
 *   "negativeCount": 4,
 *   "positivePercentage": 60.0,
 *   "neutralPercentage": 26.7,
 *   "negativePercentage": 13.3,
 *   "averageSentimentScore": 0.45,
 *   "moodDistribution": {
 *     "Happy": 10,
 *     "Relaxed": 8,
 *     "Calm": 7,
 *     "Sad": 3,
 *     "Angry": 2
 *   }
 * }
 * 
 * 🐛 DEBUG CHECKLIST:
 * ✅ Package: com.emotion.checkin.dto.response
 * ✅ @Data + @Builder
 * ✅ Percentage คำนวณให้แล้ว
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmotionStats {
    
    // Counts
    private Long totalCheckins;
    private Long positiveCount;     // Level 3
    private Long neutralCount;      // Level 2
    private Long negativeCount;     // Level 1
    
    // Percentages
    private Double positivePercentage;
    private Double neutralPercentage;
    private Double negativePercentage;
    
    // Average sentiment
    private Double averageSentimentScore;
    
    // Mood distribution (สำหรับ detailed chart)
    private Map<String, Long> moodDistribution;  // {"Happy": 10, "Sad": 5, ...}
    
    // Trend (เปรียบเทียบกับสัปดาห์ก่อน)
    private String trend;           // "IMPROVING", "DECLINING", "STABLE"
    private Double trendPercentage; // +15.5 หรือ -8.2
}