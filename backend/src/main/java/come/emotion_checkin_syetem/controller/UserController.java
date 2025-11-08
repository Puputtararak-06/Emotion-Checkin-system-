package come.emotion_checkin_syetem.controller;

import come.emotion_checkin_syetem.dto.request.AssignDepartmentRequest;
import come.emotion_checkin_syetem.dto.request.UpdateProfileRequest;
import come.emotion_checkin_syetem.dto.response.ApiResponse;
import come.emotion_checkin_syetem.dto.response.UserDTO;
import come.emotion_checkin_syetem.entity.User;
import come.emotion_checkin_syetem.service.UserManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 📍 LOCATION: src/main/java/com/emotion/checkin/controller/UserController.java
 * 
 * 👥 USER CONTROLLER - User management endpoints (CRUD)
 * 
 * ✅ Endpoints:
 * - GET    /api/users                    - List all users (Admin)
 * - GET    /api/users/search             - Search users (Admin)
 * - GET    /api/users/{id}               - Get user by ID
 * - POST   /api/users                    - Create user (Admin)
 * - PUT    /api/users/{id}               - Update user (Admin/Self)
 * - DELETE /api/users/{id}               - Deactivate user (Admin)
 * - PUT    /api/users/{id}/activate      - Activate user (Admin)
 * - GET    /api/users/department/{dept}  - List by department (HR/Admin)
 * - PUT    /api/users/assign-department  - Assign department (HR/Admin)
 * - GET    /api/users/without-department - No department (HR/Admin)
 * 
 * 🔐 Access Control:
 * - Admin: Full access (Create, Update, Delete)
 * - HR: Assign department, List employees
 * - Employee: Update own profile only
 * 
 * 🐛 DEBUG CHECKLIST:
 * ✅ @RestController + @RequestMapping
 * ✅ Role-based access control
 * ✅ Soft delete (deactivate)
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    
    private final UserManagementService userManagementService;
    
    /**
     * 📋 LIST ALL USERS (Admin only)
     * 
     * GET /api/users
     * 
     * Headers:
     * X-User-Id: 1 (Admin ID)
     * 
     * Response (200 OK):
     * {
     *   "success": true,
     *   "message": "Users retrieved",
     *   "data": [
     *     {
     *       "id": 1,
     *       "name": "John Doe",
     *       "email": "john@example.com",
     *       "role": "EMPLOYEE",
     *       "department": "IT",
     *       "position": "Developer",
     *       "isActive": true
     *     }
     *   ]
     * }
     */
    @GetMapping
    public ResponseEntity<ApiResponse> listAllUsers(
        @RequestHeader("X-User-Id") Long adminId
    ) {
        log.info("📋 GET /api/users - Admin ID: {}", adminId);
        
        try {
            List<UserDTO> users = userManagementService.listAllUsers(adminId);
            
            return ResponseEntity.ok(
                ApiResponse.success("Users retrieved", users)
            );
            
        } catch (RuntimeException e) {
            log.error("❌ Failed to list users: {}", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 🔍 SEARCH USERS (Admin only)
     * 
     * GET /api/users/search?keyword=john
     * 
     * Headers:
     * X-User-Id: 1 (Admin ID)
     * 
     * Query Params:
     * keyword: Search keyword
     * 
     * Response (200 OK):
     * {
     *   "success": true,
     *   "message": "Search results",
     *   "data": [...]
     * }
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchUsers(
        @RequestHeader("X-User-Id") Long adminId,
        @RequestParam String keyword
    ) {
        log.info("🔍 GET /api/users/search - Keyword: {}", keyword);
        
        try {
            List<UserDTO> users = userManagementService.searchUsers(adminId, keyword);
            
            return ResponseEntity.ok(
                ApiResponse.success("Search results", users)
            );
            
        } catch (RuntimeException e) {
            log.error("❌ Search failed: {}", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 👤 GET USER BY ID
     * 
     * GET /api/users/{id}
     * 
     * Response (200 OK):
     * {
     *   "success": true,
     *   "message": "User found",
     *   "data": {...}
     * }
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long id) {
        log.info("👤 GET /api/users/{}", id);
        
        try {
            UserDTO user = userManagementService.getUserById(id);
            
            return ResponseEntity.ok(
                ApiResponse.success("User found", user)
            );
            
        } catch (RuntimeException e) {
            log.error("❌ User not found: {}", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * ➕ CREATE USER (Admin only)
     * 
     * POST /api/users
     * 
     * Headers:
     * X-User-Id: 1 (Admin ID)
     * 
     * Request Body:
     * {
     *   "name": "Jane Smith",
     *   "email": "jane@example.com",
     *   "password": "password123",
     *   "role": "HR",
     *   "department": "IT",
     *   "position": "HR Manager"
     * }
     * 
     * Response (201 Created):
     * {
     *   "success": true,
     *   "message": "User created successfully",
     *   "data": {...}
     * }
     */
    @PostMapping
    public ResponseEntity<ApiResponse> createUser(
        @RequestHeader("X-User-Id") Long adminId,
        @RequestParam String name,
        @RequestParam String email,
        @RequestParam String password,
        @RequestParam User.Role role,
        @RequestParam(required = false) String department,
        @RequestParam(required = false) String position
    ) {
        log.info("➕ POST /api/users - Creating user: {}", email);
        
        try {
            UserDTO user = userManagementService.createUser(
                adminId, name, email, password, role, department, position
            );
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created successfully", user));
            
        } catch (RuntimeException e) {
            log.error("❌ User creation failed: {}", e.getMessage());
            
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * ✏️ UPDATE USER (Admin or Self)
     * 
     * PUT /api/users/{id}
     * 
     * Headers:
     * X-User-Id: 1 (Requester ID)
     * 
     * Request Body:
     * {
     *   "name": "John Updated",
     *   "email": "john.new@example.com",
     *   "position": "Senior Developer"
     * }
     * 
     * Response (200 OK):
     * {
     *   "success": true,
     *   "message": "User updated successfully",
     *   "data": {...}
     * }
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateUser(
        @RequestHeader("X-User-Id") Long requesterId,
        @PathVariable Long id,
        @Valid @RequestBody UpdateProfileRequest request
    ) {
        log.info("✏️ PUT /api/users/{} - Requester: {}", id, requesterId);
        
        try {
            UserDTO user = userManagementService.updateUser(requesterId, id, request);
            
            return ResponseEntity.ok(
                ApiResponse.success("User updated successfully", user)
            );
            
        } catch (RuntimeException e) {
            log.error("❌ User update failed: {}", e.getMessage());
            
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 🗑️ DEACTIVATE USER (Admin only) - Soft Delete
     * 
     * DELETE /api/users/{id}
     * 
     * Headers:
     * X-User-Id: 1 (Admin ID)
     * 
     * Response (200 OK):
     * {
     *   "success": true,
     *   "message": "User deactivated successfully"
     * }
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deactivateUser(
        @RequestHeader("X-User-Id") Long adminId,
        @PathVariable Long id
    ) {
        log.info("🗑️ DELETE /api/users/{} - Admin: {}", id, adminId);
        
        try {
            userManagementService.deactivateUser(adminId, id);
            
            return ResponseEntity.ok(
                ApiResponse.success("User deactivated successfully")
            );
            
        } catch (RuntimeException e) {
            log.error("❌ User deactivation failed: {}", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 🔄 ACTIVATE USER (Admin only)
     * 
     * PUT /api/users/{id}/activate
     * 
     * Headers:
     * X-User-Id: 1 (Admin ID)
     * 
     * Response (200 OK):
     * {
     *   "success": true,
     *   "message": "User activated successfully"
     * }
     */
    @PutMapping("/{id}/activate")
    public ResponseEntity<ApiResponse> activateUser(
        @RequestHeader("X-User-Id") Long adminId,
        @PathVariable Long id
    ) {
        log.info("🔄 PUT /api/users/{}/activate - Admin: {}", id, adminId);
        
        try {
            userManagementService.activateUser(adminId, id);
            
            return ResponseEntity.ok(
                ApiResponse.success("User activated successfully")
            );
            
        } catch (RuntimeException e) {
            log.error("❌ User activation failed: {}", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 🏢 LIST EMPLOYEES BY DEPARTMENT (HR/Admin)
     * 
     * GET /api/users/department/{dept}
     * 
     * Headers:
     * X-User-Id: 2 (HR ID)
     * 
     * Response (200 OK):
     * {
     *   "success": true,
     *   "message": "Employees retrieved",
     *   "data": [...]
     * }
     */
    @GetMapping("/department/{department}")
    public ResponseEntity<ApiResponse> listEmployeesByDepartment(
        @RequestHeader("X-User-Id") Long userId,
        @PathVariable String department
    ) {
        log.info("🏢 GET /api/users/department/{} - User: {}", department, userId);
        
        try {
            List<UserDTO> employees = userManagementService.listEmployeesByDepartment(
                userId, department
            );
            
            return ResponseEntity.ok(
                ApiResponse.success("Employees retrieved", employees)
            );
            
        } catch (RuntimeException e) {
            log.error("❌ Failed to list employees: {}", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 🏢 ASSIGN DEPARTMENT (HR/Admin)
     * 
     * PUT /api/users/assign-department
     * 
     * Headers:
     * X-User-Id: 2 (HR ID)
     * 
     * Request Body:
     * {
     *   "employeeId": 1,
     *   "department": "IT"
     * }
     * 
     * Response (200 OK):
     * {
     *   "success": true,
     *   "message": "Department assigned successfully"
     * }
     */
    @PutMapping("/assign-department")
    public ResponseEntity<ApiResponse> assignDepartment(
        @RequestHeader("X-User-Id") Long requesterId,
        @Valid @RequestBody AssignDepartmentRequest request
    ) {
        log.info("🏢 PUT /api/users/assign-department - Requester: {}, Employee: {}, Dept: {}",
            requesterId, request.getEmployeeId(), request.getDepartment());
        
        try {
            userManagementService.assignDepartment(requesterId, request);
            
            return ResponseEntity.ok(
                ApiResponse.success("Department assigned successfully")
            );
            
        } catch (RuntimeException e) {
            log.error("❌ Department assignment failed: {}", e.getMessage());
            
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * ⚠️ GET EMPLOYEES WITHOUT DEPARTMENT (HR/Admin)
     * 
     * GET /api/users/without-department
     * 
     * Headers:
     * X-User-Id: 2 (HR ID)
     * 
     * Response (200 OK):
     * {
     *   "success": true,
     *   "message": "Employees without department",
     *   "data": [...]
     * }
     */
    @GetMapping("/without-department")
    public ResponseEntity<ApiResponse> getEmployeesWithoutDepartment(
        @RequestHeader("X-User-Id") Long requesterId
    ) {
        log.info("⚠️ GET /api/users/without-department - Requester: {}", requesterId);
        
        try {
            List<UserDTO> employees = userManagementService.getEmployeesWithoutDepartment(
                requesterId
            );
            
            return ResponseEntity.ok(
                ApiResponse.success("Employees without department", employees)
            );
            
        } catch (RuntimeException e) {
            log.error("❌ Failed to get employees: {}", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}


