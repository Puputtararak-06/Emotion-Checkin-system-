package come.emotion_checkin_syetem.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 📍 LOCATION: src/main/java/com/emotion/checkin/dto/request/LoginRequest.java
 * 
 * 🔐 LOGIN REQUEST - ข้อมูลสำหรับ login
 * 
 * ✅ Frontend ส่งมา:
 * {
 *   "email": "john@example.com",
 *   "password": "password123"
 * }
 * 
 * ⚠️ Validations:
 * - email: required, valid email format
 * - password: required, not blank
 * 
 * 🐛 DEBUG CHECKLIST:
 * ✅ Package: com.emotion.checkin.dto.request
 * ✅ @Data (Lombok)
 * ✅ Validation annotations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    private String password;
}