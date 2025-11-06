package come.emotion_checkin_syetem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 📍 LOCATION: src/main/java/com/emotion/checkin/dto/response/NotificationDTO.java
 * 
 * 🔔 NOTIFICATION DTO - ข้อมูลแจ้งเตือน
 * 
 * ✅ ใช้ที่:
 * - Notification bell dropdown
 * - Notification page
 * 
 * 📊 Frontend จะได้:
 * {
 *   "id": 1,
 *   "message": "Employee John reported a negative mood",
 *   "senderName": "System",
 *   "senderRole": "SYSTEM",
 *   "readStatus": false,
 *   "createdAt": "2025-11-07T14:30:00",
 *   "timeAgo": "5 minutes ago",
 *   "relatedCheckinId": 123,
 *   "type": "ALERT"
 * }
 * 
 * 🐛 DEBUG CHECKLIST:
 * ✅ Package: com.emotion.checkin.dto.response
 * ✅ @Data + @Builder
 * ✅ timeAgo calculated
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    
    private Long id;
    private String message;
    
    // Sender info
    private String senderName;      // "John Doe" หรือ "System"
    private String senderRole;      // "HR", "EMPLOYEE", "SYSTEM"
    
    // Status
    private Boolean readStatus;
    
    // Timestamps
    private LocalDateTime createdAt;
    private String timeAgo;         // "5 minutes ago", "2 hours ago"
    
    // Related data
    private Long relatedCheckinId;  // link ไป check-in (optional)
    
    // Type
    private String type;            // "ALERT", "MESSAGE", "SYSTEM"
    
    // Priority
    private String priority;        // "HIGH", "NORMAL", "LOW"
}