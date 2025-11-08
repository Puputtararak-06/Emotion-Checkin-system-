package come.emotion_checkin_syetem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 📍 LOCATION: src/main/java/com/emotion/checkin/dto/request/AssignDepartmentRequest.java
 * 
 * 🏢 ASSIGN DEPARTMENT REQUEST - HR/Admin กำหนดแผนกให้พนักงาน
 * 
 * ✅ Frontend ส่งมา:
 * {
 *   "employeeId": 123,
 *   "department": "IT"
 * }
 * 
 * ⚠️ Validations:
 * - employeeId: required
 * - department: required, not blank
 * 
 * 🔧 Business Logic:
 * - เฉพาะ HR/SuperAdmin ทำได้
 * - Log ไป AuditLog
 * - แจ้งเตือน Employee ที่ได้รับการ assign
 * 
 * 🐛 DEBUG CHECKLIST:
 * ✅ Package: com.emotion.checkin.dto.request
 * ✅ @Data + Validation annotations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignDepartmentRequest {
    
    @NotNull(message = "Employee ID is required")
    private Long employeeId;
    
    @NotBlank(message = "Department is required")
    private String department;  // "IT", "Business", "Finance"
}