package come.emotion_checkin_syetem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 📍 LOCATION: src/main/java/com/emotion/checkin/dto/response/AuditLogDTO.java
 * 
 * 📝 AUDIT LOG DTO - บันทึกการกระทำ
 * 
 * ✅ ใช้ที่: Admin Audit Log page
 * 
 * 🔐 Access: SuperAdmin ONLY
 * 
 * 📊 Frontend จะได้:
 * {
 *   "id": 1,
 *   "userName": "John Doe",
 *   "userRole": "HR",
 *   "action": "ASSIGN_DEPARTMENT",
 *   "actionDescription": "กำหนดแผนก",
 *   "targetUserName": "Jane Smith",
 *   "details": {"department": "IT"},
 *   "ipAddress": "192.168.1.1",
 *   "timestamp": "2025-11-07T14:30:00",
 *   "timeAgo": "5 minutes ago",
 *   "isCritical": true
 * }
 * 
 * 🐛 DEBUG CHECKLIST:
 * ✅ Package: com.emotion.checkin.dto.response
 * ✅ @Data + @Builder
 * ✅ Action description translated
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogDTO {
    
    private Long id;
    
    // Actor (ผู้กระทำ)
    private String userName;
    private String userRole;        // "EMPLOYEE", "HR", "SUPERADMIN"
    
    // Action
    private String action;          // "LOGIN", "CHECK_IN", "ASSIGN_DEPARTMENT"
    private String actionDescription; // "เข้าสู่ระบบ", "เช็คอินอารมณ์", "กำหนดแผนก"
    
    // Target (ถ้ามี)
    private String targetUserName;
    private String targetUserRole;
    
    // Details
    private String details;         // JSON string
    
    // Network
    private String ipAddress;
    
    // Timestamps
    private LocalDateTime timestamp;
    private String timeAgo;         // "5 minutes ago"
    
    // Flags
    private Boolean isCritical;     // true ถ้าเป็น critical action
    private Boolean isAuthAction;   // true ถ้าเป็น login/logout
}