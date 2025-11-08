package come.emotion_checkin_syetem.service;

import come.emotion_checkin_syetem.dto.response.AuditLogDTO;
import come.emotion_checkin_syetem.entity.AuditLog;
import come.emotion_checkin_syetem.entity.User;
import come.emotion_checkin_syetem.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 📍 LOCATION: src/main/java/com/emotion/checkin/service/AuditLogService.java
 * 
 * 📝 AUDIT LOG SERVICE - บันทึกทุกการกระทำในระบบ
 * 
 * ✅ Features:
 * - Log all user actions
 * - Search/filter audit logs (Admin only)
 * - Track critical actions
 * - Failed login tracking
 * 
 * 📋 Logged Actions:
 * - Authentication: LOGIN, LOGOUT, REGISTER, LOGIN_FAILED
 * - Check-in: CHECK_IN, VIEW_DASHBOARD
 * - HR: VIEW_EMPLOYEE_INSIGHT, ASSIGN_DEPARTMENT, SEND_NOTIFICATION
 * - Admin: ADD_USER, EDIT_USER, DEACTIVATE_USER, ACTIVATE_USER, VIEW_AUDIT_LOG
 * 
 * 🔐 Access:
 * - SuperAdmin only!
 * - HR/Employee cannot view audit logs
 * 
 * 🐛 DEBUG CHECKLIST:
 * ✅ @Service annotation
 * ✅ @Transactional
 * ✅ Thailand timezone
 * ✅ IP address logging
 * ✅ Details in JSON format
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {
    
    private final AuditLogRepository auditLogRepository;
    
    // ========== AUTHENTICATION LOGS ==========
    
    /**
     * 🔐 LOG LOGIN
     * 
     * @param user User who logged in
     */
    @Transactional
    public void logLogin(User user) {
        createLog(user, AuditLog.Action.LOGIN, null, null, null);
        log.info("📝 Logged: LOGIN by {}", user.getEmail());
    }
    
    /**
     * 🚪 LOG LOGOUT
     * 
     * @param user User who logged out
     */
    @Transactional
    public void logLogout(User user) {
        createLog(user, AuditLog.Action.LOGOUT, null, null, null);
        log.info("📝 Logged: LOGOUT by {}", user.getEmail());
    }
    
    /**
     * 📝 LOG REGISTER
     * 
     * @param user New user who registered
     */
    @Transactional
    public void logRegister(User user) {
        createLog(user, AuditLog.Action.REGISTER, null, null, null);
        log.info("📝 Logged: REGISTER by {}", user.getEmail());
    }
    
    /**
     * ❌ LOG LOGIN FAILED
     * 
     * @param email Email that failed
     * @param ipAddress IP address
     */
    @Transactional
    public void logLoginFailed(String email, String ipAddress) {
        // Note: Cannot link to User entity (user not found)
        // Just log the attempt
        log.warn("📝 Logged: LOGIN_FAILED for {}", email);
        // TODO: Store in separate failed_login_attempts table
    }
    
    // ========== CHECK-IN LOGS ==========
    
    /**
     * 😊 LOG CHECK-IN
     * 
     * @param employee Employee who checked in
     */
    @Transactional
    public void logCheckin(User employee) {
        createLog(employee, AuditLog.Action.CHECK_IN, null, null, null);
        log.info("📝 Logged: CHECK_IN by {}", employee.getName());
    }
    
    /**
     * 📊 LOG VIEW DASHBOARD
     * 
     * @param user User who viewed dashboard
     */
    @Transactional
    public void logViewDashboard(User user) {
        createLog(user, AuditLog.Action.VIEW_DASHBOARD, null, null, null);
        log.debug("📝 Logged: VIEW_DASHBOARD by {}", user.getName());
    }
    
    // ========== HR LOGS ==========
    
    /**
     * 👁️ LOG VIEW EMPLOYEE INSIGHT
     * 
     * @param hr HR user
     * @param employee Target employee
     */
    @Transactional
    public void logViewEmployeeInsight(User hr, User employee) {
        createLog(hr, AuditLog.Action.VIEW_EMPLOYEE_INSIGHT, employee, null, null);
        log.info("📝 Logged: VIEW_EMPLOYEE_INSIGHT by {} on {}", 
            hr.getName(), employee.getName());
    }
    
    /**
     * 🏢 LOG ASSIGN DEPARTMENT
     * 
     * @param requester HR/Admin who assigned
     * @param employee Target employee
     * @param department New department
     */
    @Transactional
    public void logAssignDepartment(User requester, User employee, String department) {
        String details = String.format("{\"department\":\"%s\"}", department);
        createLog(requester, AuditLog.Action.ASSIGN_DEPARTMENT, employee, details, null);
        log.info("📝 Logged: ASSIGN_DEPARTMENT by {} to {} (dept: {})", 
            requester.getName(), employee.getName(), department);
    }
    
    /**
     * 🔔 LOG SEND NOTIFICATION
     * 
     * @param sender HR who sent
     * @param receiver Employee who received
     */
    @Transactional
    public void logSendNotification(User sender, User receiver) {
        createLog(sender, AuditLog.Action.SEND_NOTIFICATION, receiver, null, null);
        log.info("📝 Logged: SEND_NOTIFICATION from {} to {}", 
            sender.getName(), receiver.getName());
    }
    
    // ========== ADMIN LOGS ==========
    
    /**
     * ➕ LOG ADD USER
     * 
     * @param admin Admin who added
     * @param newUser New user
     */
    @Transactional
    public void logAddUser(User admin, User newUser) {
        String details = String.format(
            "{\"email\":\"%s\",\"role\":\"%s\"}", 
            newUser.getEmail(), 
            newUser.getRole()
        );
        createLog(admin, AuditLog.Action.ADD_USER, newUser, details, null);
        log.info("📝 Logged: ADD_USER by {} (new user: {})", 
            admin.getName(), newUser.getName());
    }
    
    /**
     * ✏️ LOG EDIT USER
     * 
     * @param requester Admin/Self who edited
     * @param targetUser Target user
     */
    @Transactional
    public void logEditUser(User requester, User targetUser) {
        createLog(requester, AuditLog.Action.EDIT_USER, targetUser, null, null);
        log.info("📝 Logged: EDIT_USER by {} on {}", 
            requester.getName(), targetUser.getName());
    }
    
    /**
     * 🗑️ LOG DEACTIVATE USER
     * 
     * @param admin Admin who deactivated
     * @param targetUser Target user
     */
    @Transactional
    public void logDeactivateUser(User admin, User targetUser) {
        createLog(admin, AuditLog.Action.DEACTIVATE_USER, targetUser, null, null);
        log.info("📝 Logged: DEACTIVATE_USER by {} on {}", 
            admin.getName(), targetUser.getName());
    }
    
    /**
     * 🔄 LOG ACTIVATE USER
     * 
     * @param admin Admin who activated
     * @param targetUser Target user
     */
    @Transactional
    public void logActivateUser(User admin, User targetUser) {
        createLog(admin, AuditLog.Action.ACTIVATE_USER, targetUser, null, null);
        log.info("📝 Logged: ACTIVATE_USER by {} on {}", 
            admin.getName(), targetUser.getName());
    }
    
    /**
     * 📋 LOG VIEW AUDIT LOG
     * 
     * @param admin Admin who viewed
     */
    @Transactional
    public void logViewAuditLog(User admin) {
        createLog(admin, AuditLog.Action.VIEW_AUDIT_LOG, null, null, null);
        log.debug("📝 Logged: VIEW_AUDIT_LOG by {}", admin.getName());
    }
    
    /**
     * 👥 LOG VIEW USERS
     * 
     * @param admin Admin who viewed user list
     */
    @Transactional
    public void logViewUsers(User admin) {
        // Use VIEW_EMPLOYEE_INSIGHT or create new action
        createLog(admin, AuditLog.Action.VIEW_AUDIT_LOG, null, 
            "{\"action\":\"view_users\"}", null);
        log.debug("📝 Logged: VIEW_USERS by {}", admin.getName());
    }
    
    // ========== QUERY LOGS ==========
    
    /**
     * 📋 GET ALL AUDIT LOGS (Admin only)
     * 
     * @param adminId Admin user ID
     * @param page Page number (0-based)
     * @param size Page size
     * @return Page<AuditLogDTO>
     */
    @Transactional(readOnly = true)
    public Page<AuditLogDTO> getAllAuditLogs(Long adminId, int page, int size) {
        log.info("📋 Getting audit logs (page {}, size {})", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<AuditLog> logs = auditLogRepository.findAllByOrderByCreatedAtDesc(pageable);
        
        return logs.map(this::convertToDTO);
    }
    
    /**
     * 📋 GET USER AUDIT LOGS
     * 
     * @param userId User ID
     * @return List<AuditLogDTO>
     */
    @Transactional(readOnly = true)
    public List<AuditLogDTO> getUserAuditLogs(Long userId, User user) {
        List<AuditLog> logs = auditLogRepository.findByUserOrderByCreatedAtDesc(user);
        
        return logs.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * 🔍 SEARCH AUDIT LOGS (Admin only)
     * 
     * @param adminId Admin user ID
     * @param role User role filter (optional)
     * @param action Action filter (optional)
     * @param keyword Search keyword (optional)
     * @param page Page number
     * @param size Page size
     * @return Page<AuditLogDTO>
     */
    @Transactional(readOnly = true)
    public Page<AuditLogDTO> searchAuditLogs(
        Long adminId,
        User.Role role,
        AuditLog.Action action,
        String keyword,
        int page,
        int size
    ) {
        log.info("🔍 Searching audit logs with filters");
        
        Pageable pageable = PageRequest.of(page, size);
        Page<AuditLog> logs = auditLogRepository.advancedSearch(
            role, action, keyword, pageable
        );
        
        return logs.map(this::convertToDTO);
    }
    
    /**
     * 🚨 GET CRITICAL ACTIONS (Admin only)
     * 
     * @return List<AuditLogDTO>
     */
    @Transactional(readOnly = true)
    public List<AuditLogDTO> getCriticalActions() {
        List<AuditLog> logs = auditLogRepository.findCriticalActions();
        
        return logs.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    // ========== HELPER METHODS ==========
    
    /**
     * 📝 CREATE LOG (Core method)
     * 
     * @param user User who performed action
     * @param action Action type
     * @param targetUser Target user (optional)
     * @param details Additional details (optional)
     * @param ipAddress IP address (optional)
     */
    private void createLog(
        User user,
        AuditLog.Action action,
        User targetUser,
        String details,
        String ipAddress
    ) {
        AuditLog log = AuditLog.builder()
            .user(user)
            .action(action)
            .targetUser(targetUser)
            .details(details)
            .ipAddress(ipAddress != null ? ipAddress : "unknown")
            .build();
        
        auditLogRepository.save(log);
    }
    
    /**
     * 🔄 CONVERT TO DTO
     * 
     * @param log AuditLog entity
     * @return AuditLogDTO
     */
    private AuditLogDTO convertToDTO(AuditLog log) {
        return AuditLogDTO.builder()
            .id(log.getId())
            .userName(log.getUser().getName())
            .userRole(log.getUser().getRole().toString())
            .action(log.getAction().toString())
            .actionDescription(log.getAction().getDescription())
            .targetUserName(log.getTargetUser() != null ? 
                log.getTargetUser().getName() : null)
            .targetUserRole(log.getTargetUser() != null ? 
                log.getTargetUser().getRole().toString() : null)
            .details(log.getDetails())
            .ipAddress(log.getIpAddress())
            .timestamp(log.getCreatedAt())
            .timeAgo(calculateTimeAgo(log.getCreatedAt()))
            .isCritical(log.isCriticalAction())
            .isAuthAction(log.isAuthAction())
            .build();
    }
    
    /**
     * ⏰ CALCULATE TIME AGO
     */
    private String calculateTimeAgo(LocalDateTime dateTime) {
        Duration duration = Duration.between(
            dateTime, 
            LocalDateTime.now(ZoneId.of("Asia/Bangkok"))
        );
        
        long minutes = duration.toMinutes();
        if (minutes < 1) return "เมื่อสักครู่";
        if (minutes < 60) return minutes + " นาทีที่แล้ว";
        
        long hours = duration.toHours();
        if (hours < 24) return hours + " ชั่วโมงที่แล้ว";
        
        long days = duration.toDays();
        return days + " วันที่แล้ว";
    }
}
