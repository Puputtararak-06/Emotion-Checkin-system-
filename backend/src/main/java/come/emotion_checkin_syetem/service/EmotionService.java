package come.emotion_checkin_syetem.service;

import come.emotion_checkin_syetem.dto.request.CheckinRequest;
import come.emotion_checkin_syetem.dto.response.CheckinResponse;
import come.emotion_checkin_syetem.entity.*;
import come.emotion_checkin_syetem.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

/**
 * 📍 LOCATION: src/main/java/com/emotion/checkin/service/EmotionService.java
 * 
 * 😊 EMOTION SERVICE - Check-in business logic
 * 
 * ✅ Features:
 * - Daily check-in (1 per day limit)
 * - Google NLP sentiment analysis
 * - Auto-notification if bad mood
 * - Emoji mapping
 * 
 * ⚠️ Business Rules:
 * - 1 check-in per employee per day
 * - emotionLevel: 1 (Negative), 2 (Neutral), 3 (Positive)
 * - Thailand timezone
 * - If level = 1 → notify HR
 * 
 * 🐛 DEBUG CHECKLIST:
 * ✅ @Service annotation
 * ✅ @Transactional
 * ✅ Validate 1 check-in per day
 * ✅ Google NLP integration
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmotionService {
    
    private final EmotionCheckinRepository checkinRepository;
    private final EmotionCatalogRepository catalogRepository;
    private final EmotionAIResultRepository aiResultRepository;
    private final UserRepository userRepository;
    private final GoogleNlpService googleNlpService;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;
    
    /**
     * 😊 CHECK-IN
     * 
     * ✅ Steps:
     * 1. Validate employee exists
     * 2. Check if already checked-in today
     * 3. Validate emotion type exists
     * 4. Create check-in record
     * 5. Analyze sentiment (if has comment)
     * 6. Notify HR (if bad mood)
     * 7. Log activity
     * 8. Return response
     * 
     * @param employeeId Employee ID
     * @param request CheckinRequest
     * @return CheckinResponse
     * @throws RuntimeException if validation fails
     */
    @Transactional
    public CheckinResponse checkin(Long employeeId, CheckinRequest request) {
        log.info("😊 Check-in attempt: Employee ID {}", employeeId);
        
        // Step 1: Validate employee exists
        User employee = userRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        if (!employee.isEmployee()) {
            throw new RuntimeException("Only employees can check-in");
        }
        
        // Step 2: Check if already checked-in today (Thailand timezone)
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Bangkok"));
        
        if (checkinRepository.existsByEmployeeAndCheckinDate(employee, today)) {
            log.warn("⚠️ Employee {} already checked-in today", employeeId);
            throw new RuntimeException("You have already checked-in today");
        }
        
        // Step 3: Validate emotion type exists
        EmotionCatalog emotionType = catalogRepository.findById(request.getEmotionTypeId())
            .orElseThrow(() -> new RuntimeException("Invalid emotion type"));
        
        // Validate emotion level matches catalog level
        if (!emotionType.getLevel().equals(request.getEmotionLevel())) {
            throw new RuntimeException("Emotion level does not match emotion type");
        }
        
        // Step 4: Create check-in record (Thailand timezone)
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Bangkok"));
        
        EmotionCheckin checkin = EmotionCheckin.builder()
            .employee(employee)
            .emotionLevel(request.getEmotionLevel())
            .emotionType(emotionType)
            .comment(request.getComment())
            .checkinTime(now)
            .checkinDate(today)
            .build();
        
        EmotionCheckin savedCheckin = checkinRepository.save(checkin);
        log.info("✅ Check-in created: ID {}", savedCheckin.getId());
        
        // Step 5: Analyze sentiment (if has comment)
        Float sentimentScore = null;
        String sentimentLabel = null;
        
        if (request.getComment() != null && !request.getComment().trim().isEmpty()) {
            try {
                EmotionAIResult aiResult = googleNlpService.analyzeSentiment(
                    savedCheckin, 
                    request.getComment()
                );
                sentimentScore = aiResult.getSentimentScore();
                sentimentLabel = aiResult.getSentimentLabel().toString();
                
                log.info("🤖 Sentiment analysis: score={}, label={}", sentimentScore, sentimentLabel);
            } catch (Exception e) {
                log.error("❌ Sentiment analysis failed: {}", e.getMessage());
                // Continue without AI result
            }
        }
        
        // Step 6: Notify HR (if bad mood - level 1)
        if (savedCheckin.isBadMood()) {
            log.warn("⚠️ Bad mood detected for employee: {}", employee.getName());
            notificationService.notifyHRBadMood(employee, savedCheckin);
        }
        
        // Step 7: Log activity
        auditLogService.logCheckin(employee);
        
        // Step 8: Return response
        return CheckinResponse.builder()
            .checkinId(savedCheckin.getId())
            .emoji(getEmojiForLevel(request.getEmotionLevel()))
            .mood(emotionType.getName())
            .emotionLevel(request.getEmotionLevel())
            .checkinTime(now)
            .note(request.getComment())
            .sentimentScore(sentimentScore)
            .sentimentLabel(sentimentLabel)
            .build();
    }
    
    /**
     * 📊 GET TODAY'S CHECK-IN
     * 
     * @param employeeId Employee ID
     * @return Optional<CheckinResponse>
     */
    @Transactional(readOnly = true)
    public Optional<CheckinResponse> getTodayCheckin(Long employeeId) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Bangkok"));
        
        return checkinRepository.findByEmployeeAndCheckinDate(
            userRepository.findById(employeeId).orElseThrow(),
            today
        ).map(checkin -> CheckinResponse.builder()
            .checkinId(checkin.getId())
            .emoji(getEmojiForLevel(checkin.getEmotionLevel()))
            .mood(checkin.getEmotionType().getName())
            .emotionLevel(checkin.getEmotionLevel())
            .checkinTime(checkin.getCheckinTime())
            .note(checkin.getComment())
            .build());
    }
    
    /**
     * ✅ CHECK IF CAN CHECK-IN TODAY
     * 
     * @param employeeId Employee ID
     * @return true if can check-in
     */
    public boolean canCheckinToday(Long employeeId) {
        User employee = userRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Bangkok"));
        
        return !checkinRepository.existsByEmployeeAndCheckinDate(employee, today);
    }
    
    /**
     * 😊 GET EMOJI FOR LEVEL
     * 
     * @param level Emotion level (1, 2, 3)
     * @return Emoji string
     */
    private String getEmojiForLevel(Integer level) {
        return switch (level) {
            case 1 -> "😢";  // Negative
            case 2 -> "😐";  // Neutral
            case 3 -> "😊";  // Positive
            default -> "❓";
        };
    }
}