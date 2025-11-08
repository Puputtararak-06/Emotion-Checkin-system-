package come.emotion_checkin_syetem.service;

import come.emotion_checkin_syetem.dto.request.LoginRequest;
import come.emotion_checkin_syetem.dto.request.RegisterRequest;
import come.emotion_checkin_syetem.dto.response.LoginResponse;
import come.emotion_checkin_syetem.entity.User;
import come.emotion_checkin_syetem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 📍 LOCATION: src/main/java/com/emotion/checkin/service/AuthService.java
 * 
 * 🔐 AUTH SERVICE - Authentication & Authorization
 * 
 * ✅ Features:
 * - Login with email/password
 * - Register new employee
 * - Password hashing (BCrypt)
 * - Audit logging
 * 
 * 🔒 Security:
 * - Password ต้อง BCrypt hash
 * - Email ต้อง unique
 * - Check active status
 * 
 * 🐛 DEBUG CHECKLIST:
 * ✅ @Service annotation
 * ✅ @RequiredArgsConstructor (Lombok)
 * ✅ BCryptPasswordEncoder
 * ✅ @Transactional for database operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * 🔐 LOGIN
     * 
     * ✅ Steps:
     * 1. Find user by email
     * 2. Check if user exists
     * 3. Check if user is active
     * 4. Verify password (BCrypt)
     * 5. Log successful login
     * 6. Return user data
     * 
     * @param request LoginRequest (email, password)
     * @return LoginResponse
     * @throws RuntimeException if login fails
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        log.info("🔐 Login attempt: {}", request.getEmail());
        
        // Step 1: Find user by email
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> {
                log.error("❌ User not found: {}", request.getEmail());
                // Log failed login attempt
                auditLogFailedLogin(request.getEmail());
                return new RuntimeException("Invalid email or password");
            });
        
        // Step 2: Check if user is active
        if (!user.getIsActive()) {
            log.error("❌ User is deactivated: {}", request.getEmail());
            auditLogFailedLogin(request.getEmail());
            throw new RuntimeException("Account has been deactivated. Please contact admin.");
        }
        
        // Step 3: Verify password (BCrypt)
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.error("❌ Invalid password for: {}", request.getEmail());
            auditLogFailedLogin(request.getEmail());
            throw new RuntimeException("Invalid email or password");
        }
        
        // Step 4: Log successful login
        log.info("✅ Login successful: {} ({})", user.getName(), user.getRole());
        auditLogService.logLogin(user);
        
        // Step 5: Return user data
        return LoginResponse.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .role(user.getRole().toString())
            .department(user.getDepartment())
            .position(user.getPosition())
            .build();
    }
    
    /**
     * 📝 REGISTER
     * 
     * ✅ Steps:
     * 1. Validate passwords match
     * 2. Check email not duplicate
     * 3. Hash password (BCrypt)
     * 4. Create new employee
     * 5. Save to database
     * 6. Log registration
     * 
     * @param request RegisterRequest
     * @throws RuntimeException if registration fails
     */
    @Transactional
    public void register(RegisterRequest request) {
        log.info("📝 Register attempt: {}", request.getEmail());
        
        // Step 1: Validate passwords match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            log.error("❌ Passwords do not match");
            throw new RuntimeException("Passwords do not match");
        }
        
        // Step 2: Check email not duplicate
        if (userRepository.existsByEmail(request.getEmail())) {
            log.error("❌ Email already exists: {}", request.getEmail());
            throw new RuntimeException("Email already registered");
        }
        
        // Step 3: Hash password (BCrypt)
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        
        // Step 4: Create new employee
        User newUser = User.builder()
            .name(request.getName())
            .email(request.getEmail())
            .password(hashedPassword)
            .position(request.getPosition())
            .role(User.Role.EMPLOYEE)  // Default role
            .department(null)  // HR will assign later
            .isActive(true)
            .build();
        
        // Step 5: Save to database
        User savedUser = userRepository.save(newUser);
        
        // Step 6: Log registration
        log.info("✅ Registration successful: {} ({})", savedUser.getName(), savedUser.getEmail());
        auditLogService.logRegister(savedUser);
    }
    
    /**
     * 🔒 HASH PASSWORD (Helper method)
     * 
     * @param plainPassword Plain text password
     * @return BCrypt hashed password
     */
    public String hashPassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }
    
    /**
     * ✅ VERIFY PASSWORD (Helper method)
     * 
     * @param plainPassword Plain text password
     * @param hashedPassword BCrypt hashed password
     * @return true if match
     */
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }
    
    /**
     * 📝 Log failed login attempt
     * 
     * @param email Email that failed
     */
    private void auditLogFailedLogin(String email) {
        // Note: ถ้าไม่เจอ user ก็ log ไม่ได้ แต่ควรมี
        // ในระบบจริงอาจจะเก็บ failed attempts แยกต่างหาก
        log.warn("⚠️ Failed login attempt: {}", email);
    }
}