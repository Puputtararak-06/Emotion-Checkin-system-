package come.emotion_checkin_syetem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 📍 LOCATION: src/main/java/com/emotion/checkin/dto/response/ApiResponse.java
 * 
 * 📦 API RESPONSE - Generic wrapper สำหรับทุก API response
 * 
 * ✅ Format:
 * {
 *   "success": true,
 *   "message": "Operation successful",
 *   "data": { ... }
 * }
 * 
 * ✅ ใช้ทำอะไร?
 * - Consistent response format
 * - Error handling
 * - Success messages
 * 
 * 🐛 DEBUG CHECKLIST:
 * ✅ Package: com.emotion.checkin.dto.response
 * ✅ @Data annotation (Lombok)
 * ✅ Static factory methods
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {
    
    /**
     * สถานะความสำเร็จ
     * - true: สำเร็จ
     * - false: ล้มเหลว
     */
    private boolean success;
    
    /**
     * ข้อความ (ภาษาอังกฤษ)
     * ตัวอย่าง:
     * - "Login successful"
     * - "Check-in completed"
     * - "User not found"
     */
    private String message;
    
    /**
     * ข้อมูล (optional)
     * - null ถ้าไม่มีข้อมูลเพิ่มเติม
     * - Object สำหรับ flexibility
     */
    private Object data;
    
    // ========== CONSTRUCTORS ==========
    
    /**
     * Simple message constructor
     */
    public ApiResponse(String message) {
        this.success = true;
        this.message = message;
    }
    
    /**
     * Success/Error constructor
     */
    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    // ========== FACTORY METHODS ==========
    
    /**
     * ✅ Success response (no data)
     * 
     * @param message Success message
     * @return ApiResponse
     */
    public static ApiResponse success(String message) {
        return new ApiResponse(true, message, null);
    }
    
    /**
     * ✅ Success response (with data)
     * 
     * @param message Success message
     * @param data Response data
     * @return ApiResponse
     */
    public static ApiResponse success(String message, Object data) {
        return new ApiResponse(true, message, data);
    }
    
    /**
     * ❌ Error response
     * 
     * @param message Error message
     * @return ApiResponse
     */
    public static ApiResponse error(String message) {
        return new ApiResponse(false, message, null);
    }
    
    /**
     * ❌ Error response (with data - validation errors)
     * 
     * @param message Error message
     * @param data Error details
     * @return ApiResponse
     */
    public static ApiResponse error(String message, Object data) {
        return new ApiResponse(false, message, data);
    }
}