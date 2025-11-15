package come.emotion_checkin_syetem.controller;

import come.emotion_checkin_syetem.dto.request.SendNotificationRequest;
import come.emotion_checkin_syetem.dto.response.ApiResponse;
import come.emotion_checkin_syetem.dto.response.NotificationDTO;
import come.emotion_checkin_syetem.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 📍 LOCATION: src/main/java/com/emotion/checkin/controller/NotificationController.java
 * 
 * 🔔 NOTIFICATION CONTROLLER - Notification endpoints
 * 
 * ✅ Endpoints:
 * - GET  /api/notifications              - Get all notifications
 * - GET  /api/notifications/unread       - Get unread notifications
 * - GET  /api/notifications/count-unread - Count unread
 * - POST /api/notifications              - Send notification (HR only)
 * - PUT  /api/notifications/{id}/read    - Mark as read
 * - PUT  /api/notifications/read-all     - Mark all as read
 * 
 * 🔐 Access:
 * - GET: All authenticated users
 * - POST: HR + Admin only
 * 
 * 🐛 DEBUG CHECKLIST:
 * ✅ @RestController + @RequestMapping
 * ✅ Role-based access for POST
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    
    private final NotificationService notificationService;
    
    /**
     * 📬 GET ALL NOTIFICATIONS
     * 
     * GET /api/notifications
     * 
     * Headers:
     * X-User-Id: 1
     * 
     * Response (200 OK):
     * {
     *   "success": true,
     *   "message": "Notifications retrieved",
     *   "data": [
     *     {
     *       "id": 1,
     *       "message": "Employee John reported a negative mood",
     *       "senderName": "System",
     *       "readStatus": false,
     *       "createdAt": "2025-11-07T14:30:00",
     *       "timeAgo": "5 minutes ago",
     *       "type": "ALERT",
     *       "priority": "HIGH"
     *     }
     *   ]
     * }
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getNotifications(
        @RequestHeader("X-User-Id") Long userId
    ) {
        log.info("📬 GET /api/notifications - User ID: {}", userId);
        
        List<NotificationDTO> notifications = notificationService.getNotifications(userId);
        
        return ResponseEntity.ok(
            ApiResponse.success("Notifications retrieved", notifications)
        );
    }
    
    /**
     * 🔴 GET UNREAD NOTIFICATIONS
     * 
     * GET /api/notifications/unread
     * 
     * Headers:
     * X-User-Id: 1
     * 
     * Response (200 OK):
     * {
     *   "success": true,
     *   "message": "Unread notifications retrieved",
     *   "data": [...]
     * }
     */
    @GetMapping("/unread")
    public ResponseEntity<ApiResponse> getUnreadNotifications(
        @RequestHeader("X-User-Id") Long userId
    ) {
        log.info("🔴 GET /api/notifications/unread - User ID: {}", userId);
        
        List<NotificationDTO> notifications = notificationService.getUnreadNotifications(userId);
        
        return ResponseEntity.ok(
            ApiResponse.success("Unread notifications retrieved", notifications)
        );
    }
    
    /**
     * 🔢 COUNT UNREAD NOTIFICATIONS
     * 
     * GET /api/notifications/count-unread
     * 
     * Headers:
     * X-User-Id: 1
     * 
     * Response (200 OK):
     * {
     *   "success": true,
     *   "message": "Unread count retrieved",
     *   "data": {
     *     "unreadCount": 5
     *   }
     * }
     */
    @GetMapping("/count-unread")
    public ResponseEntity<ApiResponse> countUnreadNotifications(
        @RequestHeader("X-User-Id") Long userId
    ) {
        log.info("🔢 GET /api/notifications/count-unread - User ID: {}", userId);
        
        long count = notificationService.countUnreadNotifications(userId);
        
        return ResponseEntity.ok(
            ApiResponse.success("Unread count retrieved", 
                new UnreadCountResponse(count))
        );
    }
    
    /**
     * 💬 SEND NOTIFICATION (HR/Admin only)
     * 
     * POST /api/notifications
     * 
     * Headers:
     * X-User-Id: 2 (HR ID)
     * 
     * Request Body:
     * {
     *   "receiverId": 1,
     *   "message": "Please schedule a meeting with me this week.",
     *   "relatedCheckinId": 123,
     *   "priority": "NORMAL"
     * }
     * 
     * Response (200 OK):
     * {
     *   "success": true,
     *   "message": "Notification sent successfully"
     * }
     */
    @PostMapping
    public ResponseEntity<ApiResponse> sendNotification(
        @RequestHeader("X-User-Id") Long senderId,
        @Valid @RequestBody SendNotificationRequest request
    ) {
        log.info("💬 POST /api/notifications - Sender: {}, Receiver: {}", 
            senderId, request.getReceiverId());
        
        try {
            notificationService.sendNotification(senderId, request);
            
            return ResponseEntity.ok(
                ApiResponse.success("Notification sent successfully")
            );
            
        } catch (RuntimeException e) {
            log.error("❌ Failed to send notification: {}", e.getMessage());
            
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * ✅ MARK AS READ
     * 
     * PUT /api/notifications/{id}/read
     * 
     * Response (200 OK):
     * {
     *   "success": true,
     *   "message": "Notification marked as read"
     * }
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse> markAsRead(
        @PathVariable Long id
    ) {
        log.info("✅ PUT /api/notifications/{}/read", id);
        
        notificationService.markAsRead(id);
        
        return ResponseEntity.ok(
            ApiResponse.success("Notification marked as read")
        );
    }
    
    /**
     * ✅ MARK ALL AS READ
     * 
     * PUT /api/notifications/read-all
     * 
     * Headers:
     * X-User-Id: 1
     * 
     * Response (200 OK):
     * {
     *   "success": true,
     *   "message": "All notifications marked as read"
     * }
     */
    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse> markAllAsRead(
        @RequestHeader("X-User-Id") Long userId
    ) {
        log.info("✅ PUT /api/notifications/read-all - User ID: {}", userId);
        
        notificationService.markAllAsRead(userId);
        
        return ResponseEntity.ok(
            ApiResponse.success("All notifications marked as read")
        );
    }
    
    /**
     * Helper DTO for unread count response
     */
    private record UnreadCountResponse(long unreadCount) {}
}
