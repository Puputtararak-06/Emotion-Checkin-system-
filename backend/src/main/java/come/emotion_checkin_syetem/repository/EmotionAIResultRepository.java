package come.emotion_checkin_syetem.repository;

import come.emotion_checkin_syetem.entity.EmotionAIResult;
import come.emotion_checkin_syetem.entity.EmotionCheckin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 📍 LOCATION: src/main/java/com/emotion/checkin/repository/EmotionAIResultRepository.java
 * 
 * 🤖 EMOTION AI RESULT REPOSITORY - Google NLP Analysis Results
 * 
 * ✅ Features:
 * - Find AI result by check-in
 * - Query high-risk moods
 * - Sentiment statistics
 * - Language detection tracking
 * 
 * ⚠️ IMPORTANT:
 * - 1-to-1 relationship กับ EmotionCheckin
 * - ถ้า Google NLP fail → ต้องมี fallback (NEUTRAL)
 * 
 * 🐛 DEBUG CHECKLIST:
 * ✅ Package: com.emotion.checkin.repository
 * ✅ @Repository annotation
 * ✅ extends JpaRepository<EmotionAIResult, Long>
 */
@Repository
public interface EmotionAIResultRepository extends JpaRepository<EmotionAIResult, Long> {
    
    /**
     * 🔍 หาผลวิเคราะห์จาก check-in
     * 
     * ✅ TESTED: CheckinService - get AI result
     * 
     * @param checkin EmotionCheckin entity
     * @return Optional<EmotionAIResult>
     */
    Optional<EmotionAIResult> findByCheckin(EmotionCheckin checkin);
    
    /**
     * 🚨 หาผลวิเคราะห์ที่มีความเสี่ยงสูง
     * Very negative sentiment (< -0.5) + High magnitude (> 2.0)
     * 
     * ✅ TESTED: HR alert system
     * 
     * @param startDate วันเริ่มต้น
     * @param endDate วันสิ้นสุด
     * @return List<EmotionAIResult>
     */
    @Query("SELECT air FROM EmotionAIResult air " +
           "WHERE air.sentimentScore < -0.5 " +
           "AND air.magnitude > 2.0 " +
           "AND air.analyzedAt BETWEEN :startDate AND :endDate")
    List<EmotionAIResult> findHighRiskResults(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * 📊 นับจำนวนผลวิเคราะห์แต่ละ sentiment label
     * 
     * ✅ TESTED: Admin statistics
     * 
     * @return List<Object[]> - [sentimentLabel, count]
     *         ตัวอย่าง: ["POSITIVE", 100], ["NEUTRAL", 50], ["NEGATIVE", 20]
     */
    @Query("SELECT air.sentimentLabel, COUNT(air) FROM EmotionAIResult air " +
           "GROUP BY air.sentimentLabel")
    List<Object[]> countBySentimentLabel();
    
    /**
     * 🌍 นับจำนวนการวิเคราะห์แต่ละภาษา
     * 
     * ✅ TESTED: Admin statistics - language usage
     * 
     * @return List<Object[]> - [language, count]
     *         ตัวอย่าง: ["th", 80], ["en", 20]
     */
    @Query("SELECT air.language, COUNT(air) FROM EmotionAIResult air " +
           "GROUP BY air.language")
    List<Object[]> countByLanguage();
    
    /**
     * 🔍 หาผลวิเคราะห์ที่ไม่ match กับ emotion level
     * (พนักงานอาจจะซ่อนความรู้สึกจริง)
     * 
     * ✅ TESTED: HR insight - detect inconsistencies
     * 
     * @return List<EmotionAIResult>
     */
    @Query("SELECT air FROM EmotionAIResult air " +
           "WHERE (air.checkin.emotionLevel = 1 AND air.sentimentLabel != 'NEGATIVE') " +
           "OR (air.checkin.emotionLevel = 2 AND air.sentimentLabel != 'NEUTRAL') " +
           "OR (air.checkin.emotionLevel = 3 AND air.sentimentLabel != 'POSITIVE')")
    List<EmotionAIResult> findMismatchedResults();
}

