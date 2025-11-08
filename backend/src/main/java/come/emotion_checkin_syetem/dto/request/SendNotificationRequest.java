package come.emotion_checkin_syetem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 📍 LOCATION: src/main/java/com/emotion/checkin/dto/request/SendNotificationRequest.java
 * 
 * 🔔 SEND NOTIFICATION REQUEST - HR ส่งข้อความหาพนักงาน
 * 
 * ✅ Frontend ส่งมา:
 * {
 *   "receiverId": 123,
 *   "message": "Please schedule a meeting with me this week.",
 *   "relatedCheckinId": 456,
 *   "priority": "NORMAL"
 * }
 * 
 * ⚠️ Validations:
 * - receiverId: required (Employee ID)
 * - message: required, 1-500 characters
 * - relatedCheckinId: optional
 * - priority: optional (default = NORMAL)
 * 
 * 🔧 Business Logic:
 * - เฉพาะ HR/SuperAdmin ส่งได้
 * - Log ไป AuditLog
 * - Employee ได้รับ notification ทันที
 * 
 * 🐛 DEBUG CHECKLIST:
 * ✅ Package: com.emotion.checkin.dto.request
 * ✅ @Data + Validation annotations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendNotificationRequest {
    
    @NotNull(message = "Receiver ID is required")
    private Long receiverId;  // Employee ID
    
    @NotBlank(message = "Message is required")
    @Size(min = 1, max = 500, message = "Message must be between 1 and 500 characters")
    private String message;
    
    private Long relatedCheckinId;  // Optional: link to specific check-in
    
    private String priority;  // "HIGH", "NORMAL", "LOW" (default = NORMAL)
}