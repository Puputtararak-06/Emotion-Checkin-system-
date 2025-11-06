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
 * Emotion AI Result Repository
 */
@Repository
public interface EmotionAIResultRepository extends JpaRepository<EmotionAIResult, Long> {
    
    /**
     * หาผลวิเคราะห์จาก check-in
     */
    Optional<EmotionAIResult> findByCheckin(EmotionCheckin checkin);
    
    /**
     * หาผลวิเคราะห์ที่มีความเสี่ยงสูง (very negative + high magnitude)
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
     * นับจำนวนผลวิเคราะห์แต่ละ sentiment label
     */
    @Query("SELECT air.sentimentLabel, COUNT(air) FROM EmotionAIResult air " +
           "GROUP BY air.sentimentLabel")
    List<Object[]> countBySentimentLabel();
}

