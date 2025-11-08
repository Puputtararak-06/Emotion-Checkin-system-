package come.emotion_checkin_syetem.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 📍 LOCATION: src/main/java/com/emotion/checkin/dto/request/UpdateProfileRequest.java
 * 
 * 👤 UPDATE PROFILE REQUEST - แก้ไขข้อมูลโปรไฟล์
 * 
 * ✅ Frontend ส่งมา:
 * {
 *   "name": "John Doe",
 *   "email": "john.new@example.com",
 *   "position": "Senior Developer"
 * }
 * 
 * ⚠️ Validations:
 * - name: optional, 2-100 characters
 * - email: optional, valid format
 * - position: optional, max 100 characters
 * 
 * 🔧 Business Logic:
 * - แก้ได้เฉพาะของตัวเอง (หรือ Admin แก้ให้)
 * - ถ้าเปลี่ยน email → check ว่าซ้ำหรือไม่
 * - Log ไป AuditLog
 * 
 * 🐛 DEBUG CHECKLIST:
 * ✅ Package: com.emotion.checkin.dto.request
 * ✅ @Data + Validation annotations
 * ✅ ทุก field optional (อัพเดทเฉพาะที่ส่งมา)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;
    
    @Email(message = "Invalid email format")
    private String email;
    
    @Size(max = 100, message = "Position must be less than 100 characters")
    private String position;
}
