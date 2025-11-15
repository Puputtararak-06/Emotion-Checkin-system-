package come.emotion_checkin_syetem.controller;

import come.emotion_checkin_syetem.dto.response.ApiResponse;
import come.emotion_checkin_syetem.dto.response.DashboardResponse;
import come.emotion_checkin_syetem.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 📍 LOCATION: src/main/java/com/emotion/checkin/controller/DashboardController.java
 * 
 * 📊 DASHBOARD CONTROLLER - Role-based dashboard endpoints
 * 
 * ✅ Endpoints:
 * - GET /api/dashboard/employee       - Employee dashboard
 * - GET /api/dashboard/hr             - HR dashboard
 * - GET /api/dashboard/admin          - Admin dashboard
 * 
 * 🔐 Access:
 * - /employee: Employee only
 * - /hr: HR + Admin
 * - /admin: Admin only
 * 
 * ⚠️ PRIVACY:
 * - Employee: เห็นข้อมูลตัวเอง (รวม comment)
 * - HR: เห็น department stats (ไม่มี comment!)
 * - Admin: เห็นทุกอย่าง
 * 
 * 🐛 DEBUG CHECKLIST:
 * ✅ @RestController + @RequestMapping
 * ✅ Role-based access control
 * ✅ Privacy filtering in service layer
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    /**
     * 👤 EMPLOYEE DASHBOARD
     * 
     * GET /api/dashboard/employee
     * 
     * Headers:
     * X-User-Id: 1 (Employee ID)
     * 
     * Response (200 OK):
     * {
     *   "success": true,
     *   "message": "Dashboard data retrieved",
     *   "data": {
     *     "userName": "John Doe",
     *     "userRole": "EMPLOYEE",
     *     "department": "IT",
     *     "stats": {
     *       "totalCheckins": 30,
     *       "positiveCount": 20,
     *       "neutralCount": 8,
     *       "negativeCount": 2,
     *       "positivePercentage": 66.7
     *     },
     *     "recentCheckins": [...],
     *     "checkinStreak": 5,
     *     "canCheckinToday": false,
     *     "unreadNotifications": 3
     *   }
     * }
     */
    @GetMapping("/employee")
    public ResponseEntity<ApiResponse> getEmployeeDashboard(
        @RequestHeader("X-User-Id") Long employeeId
    ) {
        log.info("👤 GET /api/dashboard/employee - Employee ID: {}", employeeId);
        
        try {
            DashboardResponse response = dashboardService.getEmployeeDashboard(employeeId);
            
            return ResponseEntity.ok(
                ApiResponse.success("Dashboard data retrieved", response)
            );
            
        } catch (RuntimeException e) {
            log.error("❌ Failed to get employee dashboard: {}", e.getMessage());
            
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 🏢 HR DASHBOARD
     * 
     * GET /api/dashboard/hr
     * 
     * Headers:
     * X-User-Id: 2 (HR ID)
     * 
     * Query Params (optional):
     * ?department=IT
     * 
     * Response (200 OK):
     * {
     *   "success": true,
     *   "message": "Dashboard data retrieved",
     *   "data": {
     *     "userName": "Jane HR",
     *     "userRole": "HR",
     *     "departments": ["IT", "Business", "Finance"],
     *     "departmentStats": [
     *       {
     *         "department": "IT",
     *         "totalEmployees": 25,
     *         "activeEmployees": 23,
     *         "checkinRate": 92.0,
     *         "todayCheckins": 20,
     *         "positiveCount": 15,
     *         "neutralCount": 4,
     *         "negativeCount": 1,
     *         "averageMoodScore": 2.7,
     *         "highRiskCount": 0
     *       }
     *     ],
     *     "employeeInsights": [
     *       {
     *         "employeeId": 1,
     *         "name": "John Doe",
     *         "lastMood": "Happy",
     *         "recentComment": null  // ⚠️ HR ไม่เห็น comment!
     *       }
     *     ],
     *     "highRiskEmployees": 2
     *   }
     * }
     */
    @GetMapping("/hr")
    public ResponseEntity<ApiResponse> getHRDashboard(
        @RequestHeader("X-User-Id") Long hrId,
        @RequestParam(required = false) String department
    ) {
        log.info("🏢 GET /api/dashboard/hr - HR ID: {}, Department: {}", 
            hrId, department);
        
        try {
            DashboardResponse response = dashboardService.getHRDashboard(hrId, department);
            
            return ResponseEntity.ok(
                ApiResponse.success("Dashboard data retrieved", response)
            );
            
        } catch (RuntimeException e) {
            log.error("❌ Failed to get HR dashboard: {}", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 👑 ADMIN DASHBOARD
     * 
     * GET /api/dashboard/admin
     * 
     * Headers:
     * X-User-Id: 3 (Admin ID)
     * 
     * Response (200 OK):
     * {
     *   "success": true,
     *   "message": "Dashboard data retrieved",
     *   "data": {
     *     "userName": "Super Admin",
     *     "userRole": "SUPERADMIN",
     *     "departments": ["IT", "Business", "Finance"],
     *     "departmentStats": [...],
     *     "employeeInsights": [
     *       {
     *         "employeeId": 1,
     *         "name": "John Doe",
     *         "lastMood": "Happy",
     *         "recentComment": "Feeling great!"  // ✅ Admin เห็น comment!
     *       }
     *     ],
     *     "highRiskEmployees": 2,
     *     "consecutiveBadMoodCount": 1
     *   }
     * }
     */
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse> getAdminDashboard(
        @RequestHeader("X-User-Id") Long adminId
    ) {
        log.info("👑 GET /api/dashboard/admin - Admin ID: {}", adminId);
        
        try {
            DashboardResponse response = dashboardService.getAdminDashboard(adminId);
            
            return ResponseEntity.ok(
                ApiResponse.success("Dashboard data retrieved", response)
            );
            
        } catch (RuntimeException e) {
            log.error("❌ Failed to get admin dashboard: {}", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}
