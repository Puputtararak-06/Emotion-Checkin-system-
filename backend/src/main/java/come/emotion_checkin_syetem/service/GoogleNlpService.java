package come.emotion_checkin_syetem.service;

import come.emotion_checkin_syetem.entity.EmotionAIResult;
import come.emotion_checkin_syetem.entity.EmotionCheckin;
import come.emotion_checkin_syetem.repository.EmotionAIResultRepository;
import com.google.cloud.language.v1.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 📍 LOCATION: src/main/java/com/emotion/checkin/service/GoogleNLPService.java
 * 
 * 🤖 GOOGLE NLP SERVICE - Sentiment Analysis
 * 
 * ✅ Features:
 * - Analyze sentiment from text (Thai/English)
 * - Extract sentiment score (-1.0 to 1.0)
 * - Extract magnitude (emotion strength)
 * - Detect language
 * - Map to sentiment label
 * 
 * 📊 Sentiment Mapping:
 * - score > 0.25  → POSITIVE
 * - score -0.25 to 0.25 → NEUTRAL
 * - score < -0.25 → NEGATIVE
 * 
 * ⚠️ IMPORTANT:
 * - Requires Google Cloud credentials
 * - API key in application.properties
 * - Has fallback if API fails
 * 
 * 🐛 DEBUG CHECKLIST:
 * ✅ @Service annotation
 * ✅ Google Cloud dependencies in pom.xml
 * ✅ Error handling (fallback to NEUTRAL)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleNlpService {
    
    private final EmotionAIResultRepository aiResultRepository;
    
    /**
     * 🤖 ANALYZE SENTIMENT
     * 
     * ✅ Steps:
     * 1. Prepare Google NLP client
     * 2. Create document from text
     * 3. Call Google NLP API
     * 4. Extract sentiment score & magnitude
     * 5. Detect language
     * 6. Map to sentiment label
     * 7. Save AI result
     * 8. Return result
     * 
     * @param checkin EmotionCheckin entity
     * @param text Comment text
     * @return EmotionAIResult
     * @throws Exception if API call fails
     */
    @Transactional
    public EmotionAIResult analyzeSentiment(EmotionCheckin checkin, String text) {
        log.info("🤖 Analyzing sentiment for check-in ID: {}", checkin.getId());
        
        try {
            // Step 1: Initialize Google NLP client
            try (LanguageServiceClient language = LanguageServiceClient.create()) {
                
                // Step 2: Create document from text
                Document doc = Document.newBuilder()
                    .setContent(text)
                    .setType(Document.Type.PLAIN_TEXT)
                    .build();
                
                // Step 3: Call Google NLP API - Analyze Sentiment
                AnalyzeSentimentResponse response = language.analyzeSentiment(doc);
                Sentiment sentiment = response.getDocumentSentiment();
                
                // Step 4: Extract score & magnitude
                float score = sentiment.getScore();
                float magnitude = sentiment.getMagnitude();
                
                // Step 5: Detect language
                String language_code = response.getLanguage();
                
                // Step 6: Map to sentiment label
                EmotionAIResult.SentimentLabel label = determineSentimentLabel(score);
                
                log.info("✅ Sentiment analysis complete: score={}, magnitude={}, label={}", 
                    score, magnitude, label);
                
                // Step 7: Save AI result
                EmotionAIResult aiResult = EmotionAIResult.builder()
                    .checkin(checkin)
                    .sentimentScore(score)
                    .magnitude(magnitude)
                    .sentimentLabel(label)
                    .language(language_code)
                    .analyzedAt(LocalDateTime.now(ZoneId.of("Asia/Bangkok")))
                    .build();
                
                return aiResultRepository.save(aiResult);
            }
            
        } catch (Exception e) {
            log.error("❌ Google NLP API failed: {}", e.getMessage());
            
            // Fallback: Create NEUTRAL result
            return createFallbackResult(checkin, text);
        }
    }
    
    /**
     * 📊 DETERMINE SENTIMENT LABEL
     * 
     * Mapping rules:
     * - score > 0.25  → POSITIVE
     * - score -0.25 to 0.25 → NEUTRAL
     * - score < -0.25 → NEGATIVE
     * 
     * @param score Sentiment score (-1.0 to 1.0)
     * @return SentimentLabel
     */
    private EmotionAIResult.SentimentLabel determineSentimentLabel(float score) {
        if (score > 0.25) return EmotionAIResult.SentimentLabel.POSITIVE;
        if (score < -0.25) return EmotionAIResult.SentimentLabel.NEGATIVE;
        return EmotionAIResult.SentimentLabel.NEUTRAL;
    }
    
    /**
     * 🔄 FALLBACK RESULT (if API fails)
     * 
     * Creates a NEUTRAL result when Google NLP API is unavailable
     * 
     * @param checkin EmotionCheckin entity
     * @param text Comment text
     * @return EmotionAIResult with NEUTRAL sentiment
     */
    private EmotionAIResult createFallbackResult(EmotionCheckin checkin, String text) {
        log.warn("⚠️ Using fallback NEUTRAL sentiment");
        
        EmotionAIResult fallback = EmotionAIResult.builder()
            .checkin(checkin)
            .sentimentScore(0.0f)
            .magnitude(0.0f)
            .sentimentLabel(EmotionAIResult.SentimentLabel.NEUTRAL)
            .language("unknown")
            .analyzedAt(LocalDateTime.now(ZoneId.of("Asia/Bangkok")))
            .build();
        
        return aiResultRepository.save(fallback);
    }
    
    /**
     * 🔍 ANALYZE SENTIMENT (Simple version - without saving)
     * 
     * For testing or preview purposes
     * 
     * @param text Text to analyze
     * @return Sentiment score
     */
    public Float analyzeSentimentSimple(String text) {
        try {
            try (LanguageServiceClient language = LanguageServiceClient.create()) {
                Document doc = Document.newBuilder()
                    .setContent(text)
                    .setType(Document.Type.PLAIN_TEXT)
                    .build();
                
                AnalyzeSentimentResponse response = language.analyzeSentiment(doc);
                return response.getDocumentSentiment().getScore();
            }
        } catch (Exception e) {
            log.error("❌ Sentiment analysis failed: {}", e.getMessage());
            return 0.0f;  // Neutral fallback
        }
    }

    public Sentiment analyze(String comment) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'analyze'");
    }
}