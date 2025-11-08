package come.emotion_checkin_syetem.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 📍 LOCATION: src/main/java/com/emotion/checkin/dto/request/RegisterRequest.java
 * 
 * 📝 REGISTER REQUEST - ข้อมูลสำหรับสมัครสมาชิก
 * 
 * ✅ Frontend ส่งมา:
 * {
 *   "name": "John Doe",
 *   "email": "john@example.com",
 *   "password": "password123",
 *   "confirmPassword": "password123",
 *   "position": "Developer"
 * }
 * 
 * ⚠️ Validations:
 * - name: required, 2-100 characters
 * - email: required, valid format, unique
 * - password: required, min 8 characters
 * - confirmPassword: must match password
 * - position: optional
 * 
 * 🔧 Business Logic:
 * - role = EMPLOYEE (default)
 * - department = NULL (HR จะ assign ทีหลัง)
 * - isActive = true
 * 
 * 🐛 DEBUG CHECKLIST:
 * ✅ Package: com.emotion.checkin.dto.request
 * ✅ @Data + Validation annotations
 * ✅ Password match validation (ใน Service)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
    
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
    
    @Size(max = 100, message = "Position must be less than 100 characters")
    private String position;
}