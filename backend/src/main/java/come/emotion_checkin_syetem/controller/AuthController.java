package come.emotion_checkin_syetem.controller;

import come.emotion_checkin_syetem.dto.request.LoginRequest;
import come.emotion_checkin_syetem.dto.request.RegisterRequest;
import come.emotion_checkin_syetem.dto.response.ApiResponse;
import come.emotion_checkin_syetem.dto.response.LoginResponse;
import come.emotion_checkin_syetem.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 📍 LOCATION: src/main/java/com/emotion/checkin/controller/AuthController.java
 * 
 * 🔐 AUTH CONTROLLER - Authentication endpoints
 * 
 * ✅ Endpoints:
 * - POST /api/auth/login       - User login
 * - POST /api/auth/register    - Employee registration
 * 
 * 🔓 Access: Public (no authentication required)
 * 
 * 🐛 DEBUG CHECKLIST:
 * ✅ @RestController annotation
 * ✅ @RequestMapping("/api/auth")
 * ✅ @Valid for request validation
 * ✅ Try-catch error handling
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * 🔐 LOGIN
     * 
     * POST /api/auth/login
     * 
     * Request Body:
     * {
     *   "email": "john@example.com",
     *   "password": "password123"
     * }
     * 
     * Response (200 OK):
     * {
     *   "success": true,
     *   "message": "Login successful",
     *   "data": {
     *     "id": 1,
     *     "name": "John Doe",
     *     "email": "john@example.com",
     *     "role": "EMPLOYEE",
     *     "department": "IT",
     *     "position": "Developer"
     *   }
     * }
     * 
     * Response (401 Unauthorized):
     * {
     *   "success": false,
     *   "message": "Invalid email or password"
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("🔐 POST /api/auth/login - Email: {}", request.getEmail());
        
        try {
            LoginResponse response = authService.login(request);
            
            return ResponseEntity.ok(
                ApiResponse.success("Login successful", response)
            );
            
        } catch (RuntimeException e) {
            log.error("❌ Login failed: {}", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 📝 REGISTER
     * 
     * POST /api/auth/register
     * 
     * Request Body:
     * {
     *   "name": "John Doe",
     *   "email": "john@example.com",
     *   "password": "password123",
     *   "confirmPassword": "password123",
     *   "position": "Developer"
     * }
     * 
     * Response (201 Created):
     * {
     *   "success": true,
     *   "message": "Registration successful. Please wait for HR to assign your department."
     * }
     * 
     * Response (400 Bad Request):
     * {
     *   "success": false,
     *   "message": "Email already registered"
     * }
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("📝 POST /api/auth/register - Email: {}", request.getEmail());
        
        try {
            authService.register(request);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                    "Registration successful. Please wait for HR to assign your department."
                ));
            
        } catch (RuntimeException e) {
            log.error("❌ Registration failed: {}", e.getMessage());
            
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}