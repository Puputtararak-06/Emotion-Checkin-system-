package come.emotion_checkin_syetem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 📍 LOCATION: src/main/java/com/emotion/checkin/dto/response/UserDTO.java
 * 
 * 👤 USER DTO - ข้อมูล user ทั่วไป
 * 
 * ✅ ใช้ที่:
 * - User list (Admin)
 * - Profile page
 * - Search results
 * 
 * ⚠️ SECURITY:
 * - ไม่มี password!
 * - ไม่มี sensitive data
 * 
 * 📊 Frontend จะได้:
 * {
 *   "id": 1,
 *   "name": "John Doe",
 *   "email": "john@example.com",
 *   "role": "EMPLOYEE",
 *   "department": "IT",
 *   "position": "Developer",
 *   "isActive": true,
 *   "createdAt": "2025-01-01T00:00:00",
 *   "lastLogin": "2025-11-07T14:30:00"
 * }
 * 
 * 🐛 DEBUG CHECKLIST:
 * ✅ Package: com.emotion.checkin.dto.response
 * ✅ @Data + @Builder
 * ✅ ไม่มี password field!
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    
    private Long id;
    private String name;
    private String email;
    private String role;        // "EMPLOYEE", "HR", "SUPERADMIN"
    private String department;
    private String position;
    private Boolean isActive;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    
    // Stats (optional)
    private Integer totalCheckins;
    private LocalDateTime lastCheckin;
}