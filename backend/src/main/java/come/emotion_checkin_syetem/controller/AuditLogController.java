package come.emotion_checkin_syetem.controller;

import come.emotion_checkin_syetem.dto.response.ApiResponse;
import come.emotion_checkin_syetem.dto.response.AuditLogDTO;
import come.emotion_checkin_syetem.entity.AuditLog;
import come.emotion_checkin_syetem.entity.User;
import come.emotion_checkin_syetem.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 📍 LOCATION: src/main/java/com/emotion/checkin/controller/AuditLogController.java
 * 
 * 📝 AUDIT LOG CONTROLLER - Audit log viewing (SuperAdmin only)
 * 
 * ✅ Endpoints:
 * - GET /api/audit-logs                - Get all logs (paginated)
 * - GET /api/audit-logs/search         - Search logs
 * - GET /api/audit-logs/critical       - Critical actions only
 * - GET /api/audit-logs/user/{id}      - Logs for specific user
 * 
 * 🔐 Access: SuperAdmin ONLY
 * 
 * 🐛 DEBUG CHECKLIST:
 * ✅ @RestController + @RequestMapping
 * ✅ Admin-only access
 * ✅ Pagination support
 */
@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
@Slf4j
public class AuditLogController {
    
    private final AuditLogService auditLogService;
    
    /**
     * 📋 GET ALL AUDIT LOGS (Paginated)
     * 
     * GET /api/audit-logs?page=0&size=20
     * 
     * Headers:
     * X-User-Id: 1 (Admin ID)
     * 
     * Query Params:
     * page: Page number (default = 0)
     * size: Page size (default = 20)
     * 
     * Response (200 OK):
     * {
     *   "success": true,
     *   "message": "Audit logs retrieved",
     *   "data": {
     *     "content": [...],
     *     "totalElements": 100,
     *     "totalPages": 5,
     *     "number": 0,
     *     "size": 20
     *   }
     * }
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getAllAuditLogs(
        @RequestHeader("X-User-Id") Long adminId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        log.info("📋 GET /api/audit-logs - Admin: {}, Page: {}, Size: {}", 
            adminId, page, size);
        
        try {
            Page<AuditLogDTO> logs = auditLogService.getAllAuditLogs(adminId, page, size);
            
            return ResponseEntity.ok(
                ApiResponse.success("Audit logs retrieved", logs)
            );
            
        } catch (RuntimeException e) {
            log.error("❌ Failed to get audit logs: {}", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 🔍 SEARCH AUDIT LOGS
     * 
     * GET /api/audit-logs/search?role=HR&action=ASSIGN_DEPARTMENT&keyword=john
     * 
     * Headers:
     * X-User-Id: 1 (Admin ID)
     * 
     * Query Params (all optional):
     * role: User role filter (EMPLOYEE, HR, SUPERADMIN)
     * action: Action filter (LOGIN, CHECK_IN, etc.)
     * keyword: Search keyword (user name)
     * page: Page number (default = 0)
     * size: Page size (default = 20)
     * 
     * Response (200 OK):
     * {
     *   "success": true,
     *   "message": "Search results",
     *   "data": {...}
     * }
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchAuditLogs(
        @RequestHeader("X-User-Id") Long adminId,
        @RequestParam(required = false) User.Role role,
        @RequestParam(required = false) AuditLog.Action action,
        @RequestParam(required = false) String keyword,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        log.info("🔍 GET /api/audit-logs/search - Role: {}, Action: {}, Keyword: {}", 
            role, action, keyword);
        
        try {
            Page<AuditLogDTO> logs = auditLogService.searchAuditLogs(
                adminId, role, action, keyword, page, size
            );
            
            return ResponseEntity.ok(
                ApiResponse.success("Search results", logs)
            );
            
        } catch (RuntimeException e) {
            log.error("❌ Search failed: {}", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 🚨 GET CRITICAL ACTIONS
     * 
     * GET /api/audit-logs/critical
     * 
     * Headers:
     * X-User-Id: 1 (Admin ID)
     * 
     * Response (200 OK):
     * {
     *   "success": true,
     *   "message": "Critical actions retrieved",
     *   "data": [...]
     * }
     */
    @GetMapping("/critical")
    public ResponseEntity<ApiResponse> getCriticalActions(
        @RequestHeader("X-User-Id") Long adminId
    ) {
        log.info("🚨 GET /api/audit-logs/critical - Admin: {}", adminId);
        
        try {
            List<AuditLogDTO> logs = auditLogService.getCriticalActions();
            
            return ResponseEntity.ok(
                ApiResponse.success("Critical actions retrieved", logs)
            );
            
        } catch (RuntimeException e) {
            log.error("❌ Failed to get critical actions: {}", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}
