package come.emotion_checkin_syetem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 📍 LOCATION: src/main/java/com/emotion/checkin/dto/response/LoginResponse.java
 * 
 * 🔐 LOGIN RESPONSE - ข้อมูลหลัง login สำเร็จ
 * 
 * ✅ Frontend จะได้:
 * {
 *   "id": 1,
 *   "name": "John Doe",
 *   "email": "john@example.com",
 *   "role": "EMPLOYEE",
 *   "department": "IT",
 *   "position": "Developer"
 * }
 * 
 * ⚠️ SECURITY:
 * - ห้ามส่ง password กลับไป!
 * - ใช้ข้อมูลนี้เก็บใน localStorage
 * 
 * 🐛 DEBUG CHECKLIST:
 * ✅ Package: com.emotion.checkin.dto.response
 * ✅ @Data + @Builder (Lombok)
 * ✅ ไม่มี password field!
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    
    private Long id;
    private String name;
    private String email;
    private String role;        // "EMPLOYEE", "HR", "SUPERADMIN"
    private String department;  // null สำหรับ HR/SuperAdmin
    private String position;    // null สำหรับ HR/SuperAdmin
}