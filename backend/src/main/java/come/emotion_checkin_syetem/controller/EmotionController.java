package come.emotion_checkin_syetem.controller;

import come.emotion_checkin_syetem.dto.request.CheckinRequest;
import come.emotion_checkin_syetem.dto.response.ApiResponse;
import come.emotion_checkin_syetem.dto.response.CheckinResponse;
import come.emotion_checkin_syetem.service.EmotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * 📍 LOCATION: src/main/java/com/emotion/checkin/controller/EmotionController.java
 * 
 * 😊 EMOTION CONTROLLER - Check-in endpoints
 * 
 * ✅ Endpoints:
 * - POST /api/checkin              - Create check-in (Employee only)
 * - GET  /api/checkin/today        - Get today's check-in
 * - GET  /api/checkin/can-checkin  - Check if can check-in today
 * 
 * 🔐 Access: Employee only
 * 
 * ⚠️ Business Rule: 1 check-in per day
 * 
 * 🐛 DEBUG CHECKLIST:
 * ✅ @RestController + @RequestMapping
 * ✅ Employee ID from request header (or session)
 * ✅ Validation
 */
@RestController
@RequestMapping("/api/checkin")
@RequiredArgsConstructor
@Slf4j
public class EmotionController {
    
    private final EmotionService emotionService;
    
    /**
     * 😊 CHECK-IN
     * 
     * POST /api/checkin
     * 
     * Headers:
     * X-User-Id: 1  (Employee ID - from session/token)
     * 
     * Request Body:
     * {
     *   "emotionLevel": 3,
     *   "emotionTypeId": 11,
     *   "comment": "Feeling great today!"
     * }
     * 
     * Response (200 OK):
     * {
     *   "success": true,
     *   "message": "Check-in completed successfully",
     *   "data": {
     *     "checkinId": 123,
     *     "emoji": "😊",
     *     "mood": "Happy",
     *     "emotionLevel": 3,
     *     "checkinTime": "2025-11-07T14:30:00",
     *     "note": "Feeling great today!",
     *     "sentimentScore": 0.85,
     *     "sentimentLabel": "POSITIVE"
     *   }
     * }
     * 
     * Response (400 Bad Request):
     * {
     *   "success": false,
     *   "message": "You have already checked-in today"
     * }
     */
    @PostMapping
    public ResponseEntity<ApiResponse> checkin(
        @RequestHeader("X-User-Id") Long employeeId,
        @Valid @RequestBody CheckinRequest request
    ) {
        log.info("😊 POST /api/checkin - Employee ID: {}, Level: {}", 
            employeeId, request.getEmotionLevel());
        
        try {
            CheckinResponse response = emotionService.checkin(employeeId, request);
            
            return ResponseEntity.ok(
                ApiResponse.success("Check-in completed successfully", response)
            );
            
        } catch (RuntimeException e) {
            log.error("❌ Check-in failed: {}", e.getMessage());
            
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 📅 GET TODAY'S CHECK-IN
     * 
     * GET /api/checkin/today
     * 
     * Headers:
     * X-User-Id: 1
     * 
     * Response (200 OK):
     * {
     *   "success": true,
     *   "message": "Check-in found",
     *   "data": {
     *     "checkinId": 123,
     *     "emoji": "😊",
     *     "mood": "Happy",
     *     "emotionLevel": 3,
     *     "checkinTime": "2025-11-07T09:30:00"
     *   }
     * }
     * 
     * Response (404 Not Found):
     * {
     *   "success": false,
     *   "message": "No check-in found for today"
     * }
     */
    @GetMapping("/today")
    public ResponseEntity<ApiResponse> getTodayCheckin(
        @RequestHeader("X-User-Id") Long employeeId
    ) {
        log.info("📅 GET /api/checkin/today - Employee ID: {}", employeeId);
        
        Optional<CheckinResponse> response = emotionService.getTodayCheckin(employeeId);
        
        if (response.isPresent()) {
            return ResponseEntity.ok(
                ApiResponse.success("Check-in found", response.get())
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("No check-in found for today"));
        }
    }
    
    /**
     * ✅ CAN CHECK-IN TODAY?
     * 
     * GET /api/checkin/can-checkin
     * 
     * Headers:
     * X-User-Id: 1
     * 
     * Response (200 OK):
     * {
     *   "success": true,
     *   "message": "Can check-in",
     *   "data": {
     *     "canCheckin": true
     *   }
     * }
     */
    @GetMapping("/can-checkin")
    public ResponseEntity<ApiResponse> canCheckinToday(
        @RequestHeader("X-User-Id") Long employeeId
    ) {
        log.info("✅ GET /api/checkin/can-checkin - Employee ID: {}", employeeId);
        
        boolean canCheckin = emotionService.canCheckinToday(employeeId);
        
        return ResponseEntity.ok(
            ApiResponse.success(
                canCheckin ? "Can check-in" : "Already checked-in today",
                new CanCheckinResponse(canCheckin)
            )
        );
    }
    
    /**
     * Helper DTO for can-checkin response
     */
    private record CanCheckinResponse(boolean canCheckin) {}
}
