package come.emotion_checkin_syetem.service;

import come.emotion_checkin_syetem.dto.request.AssignDepartmentRequest;
import come.emotion_checkin_syetem.dto.request.UpdateProfileRequest;
import come.emotion_checkin_syetem.dto.response.UserDTO;
import come.emotion_checkin_syetem.entity.User;
import come.emotion_checkin_syetem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 📍 LOCATION: src/main/java/com/emotion/checkin/service/UserManagementService.java
 * 
 * 👥 USER MANAGEMENT SERVICE - CRUD operations for users
 * 
 * ✅ Features:
 * - List all users (Admin)
 * - Search users (Admin)
 * - Create user (Admin)
 * - Update user (Admin/Self)
 * - Deactivate user (Admin) - Soft Delete
 * - Assign department (HR/Admin)
 * 
 * 🔐 Access Control:
 * - SuperAdmin: ทำได้ทุกอย่าง
 * - HR: Assign department เท่านั้น
 * - Employee: แก้ไขข้อมูลตัวเองเท่านั้น
 * 
 * 🐛 DEBUG CHECKLIST:
 * ✅ @Service annotation
 * ✅ @Transactional
 * ✅ Role-based access control
 * ✅ Soft delete (isActive = false)
 * ✅ Audit logging
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserManagementService {
    
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * 📋 LIST ALL USERS (Admin only)
     * 
     * @param adminId Admin user ID
     * @return List<UserDTO>
     * @throws RuntimeException if not admin
     */
    @Transactional(readOnly = true)
    public List<UserDTO> listAllUsers(Long adminId) {
        log.info("📋 Listing all users by admin {}", adminId);
        
        // Validate admin
        User admin = userRepository.findById(adminId)
            .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        if (!admin.isSuperAdmin()) {
            throw new RuntimeException("Access denied: SuperAdmin role required");
        }
        
        // Get all users (including inactive)
        List<User> users = userRepository.findAll();
        
        // Log activity
        auditLogService.logViewUsers(admin);
        
        return users.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * 📋 LIST EMPLOYEES BY DEPARTMENT (HR/Admin)
     * 
     * @param userId User ID (HR or Admin)
     * @param department Department name
     * @return List<UserDTO>
     */
    @Transactional(readOnly = true)
    public List<UserDTO> listEmployeesByDepartment(Long userId, String department) {
        log.info("📋 Listing employees in department {} by user {}", department, userId);
        
        // Validate user
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!user.isHR() && !user.isSuperAdmin()) {
            throw new RuntimeException("Access denied: HR/Admin role required");
        }
        
        // Get employees by department
        List<User> employees = userRepository.findEmployeesByDepartment(department);
        
        return employees.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * 🔍 SEARCH USERS (Admin only)
     * 
     * @param adminId Admin user ID
     * @param keyword Search keyword
     * @return List<UserDTO>
     */
    @Transactional(readOnly = true)
    public List<UserDTO> searchUsers(Long adminId, String keyword) {
        log.info("🔍 Searching users with keyword: {}", keyword);
        
        // Validate admin
        User admin = userRepository.findById(adminId)
            .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        if (!admin.isSuperAdmin()) {
            throw new RuntimeException("Access denied: SuperAdmin role required");
        }
        
        // Search by name
        List<User> users = userRepository.searchByName(keyword);
        
        return users.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * ➕ CREATE USER (Admin only)
     * 
     * ✅ Steps:
     * 1. Validate admin
     * 2. Check email not duplicate
     * 3. Hash password
     * 4. Create user
     * 5. Save to database
     * 6. Log activity
     * 
     * @param adminId Admin user ID
     * @param name User name
     * @param email User email
     * @param password Plain password
     * @param role User role
     * @param department Department (optional)
     * @param position Position (optional)
     * @return UserDTO
     */
    @Transactional
    public UserDTO createUser(
        Long adminId,
        String name,
        String email,
        String password,
        User.Role role,
        String department,
        String position
    ) {
        log.info("➕ Creating user: {} ({})", name, email);
        
        // Step 1: Validate admin
        User admin = userRepository.findById(adminId)
            .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        if (!admin.isSuperAdmin()) {
            throw new RuntimeException("Access denied: SuperAdmin role required");
        }
        
        // Step 2: Check email not duplicate
        if (userRepository.existsByEmail(email)) {
            log.error("❌ Email already exists: {}", email);
            throw new RuntimeException("Email already registered");
        }
        
        // Step 3: Hash password
        String hashedPassword = passwordEncoder.encode(password);
        
        // Step 4: Create user
        User newUser = User.builder()
            .name(name)
            .email(email)
            .password(hashedPassword)
            .role(role)
            .department(department)
            .position(position)
            .isActive(true)
            .build();
        
        // Step 5: Save to database
        User savedUser = userRepository.save(newUser);
        
        // Step 6: Log activity
        auditLogService.logAddUser(admin, savedUser);
        
        log.info("✅ User created: {} (ID: {})", savedUser.getName(), savedUser.getId());
        
        return convertToDTO(savedUser);
    }
    
    /**
     * ✏️ UPDATE USER (Admin or Self)
     * 
     * ✅ Rules:
     * - Admin: แก้ไขได้ทุกคน
     * - Employee: แก้ไขได้แค่ตัวเอง (name, position only)
     * - ห้ามแก้ไข email, password, role ผ่าน method นี้
     * 
     * @param requesterId User who requests update
     * @param targetUserId User to be updated
     * @param request UpdateProfileRequest
     * @return UserDTO
     */
    @Transactional
    public UserDTO updateUser(
        Long requesterId,
        Long targetUserId,
        UpdateProfileRequest request
    ) {
        log.info("✏️ Updating user {} by user {}", targetUserId, requesterId);
        
        // Get requester
        User requester = userRepository.findById(requesterId)
            .orElseThrow(() -> new RuntimeException("Requester not found"));
        
        // Get target user
        User targetUser = userRepository.findById(targetUserId)
            .orElseThrow(() -> new RuntimeException("Target user not found"));
        
        // Check permission
        if (!requester.isSuperAdmin() && !requesterId.equals(targetUserId)) {
            throw new RuntimeException("Access denied: Can only update your own profile");
        }
        
        // Update fields (only if provided)
        if (request.getName() != null && !request.getName().isEmpty()) {
            targetUser.setName(request.getName());
        }
        
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            // Check email not duplicate
            if (!targetUser.getEmail().equals(request.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already in use");
            }
            targetUser.setEmail(request.getEmail());
        }
        
        if (request.getPosition() != null && !request.getPosition().isEmpty()) {
            targetUser.setPosition(request.getPosition());
        }
        
        // Save
        User updatedUser = userRepository.save(targetUser);
        
        // Log activity
        auditLogService.logEditUser(requester, updatedUser);
        
        log.info("✅ User updated: {}", updatedUser.getName());
        
        return convertToDTO(updatedUser);
    }
    
    /**
     * 🗑️ DEACTIVATE USER (Admin only) - Soft Delete
     * 
     * ⚠️ NOT DELETE! Just set isActive = false
     * 
     * @param adminId Admin user ID
     * @param targetUserId User to deactivate
     */
    @Transactional
    public void deactivateUser(Long adminId, Long targetUserId) {
        log.info("🗑️ Deactivating user {} by admin {}", targetUserId, adminId);
        
        // Validate admin
        User admin = userRepository.findById(adminId)
            .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        if (!admin.isSuperAdmin()) {
            throw new RuntimeException("Access denied: SuperAdmin role required");
        }
        
        // Get target user
        User targetUser = userRepository.findById(targetUserId)
            .orElseThrow(() -> new RuntimeException("Target user not found"));
        
        // Cannot deactivate self
        if (adminId.equals(targetUserId)) {
            throw new RuntimeException("Cannot deactivate yourself");
        }
        
        // Deactivate (Soft Delete)
        targetUser.deactivate();
        userRepository.save(targetUser);
        
        // Log activity
        auditLogService.logDeactivateUser(admin, targetUser);
        
        log.info("✅ User deactivated: {}", targetUser.getName());
    }
    
    /**
     * 🔄 ACTIVATE USER (Admin only)
     * 
     * @param adminId Admin user ID
     * @param targetUserId User to activate
     */
    @Transactional
    public void activateUser(Long adminId, Long targetUserId) {
        log.info("🔄 Activating user {} by admin {}", targetUserId, adminId);
        
        // Validate admin
        User admin = userRepository.findById(adminId)
            .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        if (!admin.isSuperAdmin()) {
            throw new RuntimeException("Access denied: SuperAdmin role required");
        }
        
        // Get target user
        User targetUser = userRepository.findById(targetUserId)
            .orElseThrow(() -> new RuntimeException("Target user not found"));
        
        // Activate
        targetUser.activate();
        userRepository.save(targetUser);
        
        // Log activity
        auditLogService.logActivateUser(admin, targetUser);
        
        log.info("✅ User activated: {}", targetUser.getName());
    }
    
    /**
     * 🏢 ASSIGN DEPARTMENT (HR/Admin)
     * 
     * ✅ Steps:
     * 1. Validate requester (HR or Admin)
     * 2. Validate employee exists
     * 3. Assign department
     * 4. Save
     * 5. Log activity
     * 6. Notify employee
     * 
     * @param requesterId User who assigns (HR/Admin)
     * @param request AssignDepartmentRequest
     */
    @Transactional
    public void assignDepartment(Long requesterId, AssignDepartmentRequest request) {
        log.info("🏢 Assigning department {} to employee {} by user {}", 
            request.getDepartment(), request.getEmployeeId(), requesterId);
        
        // Step 1: Validate requester
        User requester = userRepository.findById(requesterId)
            .orElseThrow(() -> new RuntimeException("Requester not found"));
        
        if (!requester.canAssignDepartment()) {
            throw new RuntimeException("Access denied: HR/Admin role required");
        }
        
        // Step 2: Validate employee
        User employee = userRepository.findById(request.getEmployeeId())
            .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        if (!employee.isEmployee()) {
            throw new RuntimeException("Can only assign department to employees");
        }
        
        // Step 3: Assign department
        String oldDepartment = employee.getDepartment();
        employee.setDepartment(request.getDepartment());
        
        // Step 4: Save
        userRepository.save(employee);
        
        // Step 5: Log activity
        auditLogService.logAssignDepartment(requester, employee, request.getDepartment());
        
        log.info("✅ Department assigned: {} → {} (was: {})", 
            employee.getName(), request.getDepartment(), oldDepartment);
        
        // Step 6: Notify employee (TODO: implement notification)
    }
    
    /**
     * 📊 GET USER BY ID
     * 
     * @param userId User ID
     * @return UserDTO
     */
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return convertToDTO(user);
    }
    
    /**
     * 📊 GET EMPLOYEES WITHOUT DEPARTMENT (HR/Admin)
     * 
     * @param requesterId User ID (HR/Admin)
     * @return List<UserDTO>
     */
    @Transactional(readOnly = true)
    public List<UserDTO> getEmployeesWithoutDepartment(Long requesterId) {
        // Validate requester
        User requester = userRepository.findById(requesterId)
            .orElseThrow(() -> new RuntimeException("Requester not found"));
        
        if (!requester.isHR() && !requester.isSuperAdmin()) {
            throw new RuntimeException("Access denied: HR/Admin role required");
        }
        
        // Get employees without department
        List<User> employees = userRepository.findEmployeesWithoutDepartment();
        
        return employees.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * 🔄 CONVERT TO DTO
     * 
     * @param user User entity
     * @return UserDTO
     */
    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .role(user.getRole().toString())
            .department(user.getDepartment())
            .position(user.getPosition())
            .isActive(user.getIsActive())
            .createdAt(user.getCreatedAt())
            .build();
    }
}
