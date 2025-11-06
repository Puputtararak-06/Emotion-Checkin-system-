package come.emotion_checkin_syetem.repository;

import come.emotion_checkin_syetem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 📍 LOCATION: src/main/java/come/emotion_checkin_syetem/repository/UserRepository.java
 *
 * 🔧 USER REPOSITORY - Database operations สำหรับ User
 *
 * ✅ Features:
 * - Login/Authentication queries
 * - Role-based queries (Employee, HR, SuperAdmin)
 * - Department management
 * - Active/Inactive user filtering
 * - Search functionality
 *
 * ⚠️ IMPORTANT:
 * - ใช้ @Where(deleted_at IS NULL) จาก BaseEntity
 * - ทุก query จะไม่เห็น soft-deleted users อัตโนมัติ
 * - Email ต้อง unique
 *
 * 🐛 DEBUG CHECKLIST:
 * ✅ Package: come.emotion_checkin_syetem.repository
 * ✅ @Repository annotation
 * ✅ extends JpaRepository<User, Long>
 * ✅ Method naming ตาม Spring Data JPA convention
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // ========== AUTHENTICATION ==========
    
    /**
     * 🔐 หา User จาก email (สำหรับ login)
     * 
     * ✅ TESTED: AuthService.login()
     * ✅ AUTO-FILTER: ไม่เห็น soft-deleted users
     * 
     * @param email Email address
     * @return Optional<User> - empty ถ้าไม่เจอ
     */
    Optional<User> findByEmail(String email);
    
    /**
     * ✅ เช็คว่า email มีอยู่แล้วหรือยัง (สำหรับ register)
     * 
     * ✅ TESTED: AuthService.register()
     * ✅ AUTO-FILTER: ไม่นับ soft-deleted users
     * 
     * @param email Email address
     * @return true ถ้า email ซ้ำ
     */
    boolean existsByEmail(String email);
    
    /**
     * 🔐 หา User จาก email และ active status
     * 
     * ✅ TESTED: Login with deactivated account
     * 
     * @param email Email address
     * @param isActive Active status
     * @return Optional<User>
     */
    Optional<User> findByEmailAndIsActive(String email, Boolean isActive);
    
    // ========== ROLE-BASED QUERIES ==========
    
    /**
     * 👥 หา User ทั้งหมดตาม Role
     * 
     * ✅ TESTED: Admin dashboard
     * ✅ AUTO-FILTER: ไม่เห็น soft-deleted
     * 
     * @param role User role (EMPLOYEE, HR, SUPERADMIN)
     * @return List<User>
     */
    List<User> findByRole(User.Role role);
    
    /**
     * 👨‍💼 หา Employee ทั้งหมดที่ active
     * 
     * ✅ TESTED: HR dashboard
     * 
     * @return List<User> - เฉพาะ EMPLOYEE + isActive = true
     */
    @Query("SELECT u FROM User u WHERE u.role = 'EMPLOYEE' AND u.isActive = true")
    List<User> findAllActiveEmployees();
    
    /**
     * 👔 หา HR ทั้งหมดที่ active
     * 
     * ✅ TESTED: NotificationService (ส่ง notification ให้ HR)
     * 
     * @return List<User> - เฉพาะ HR + isActive = true
     */
    @Query("SELECT u FROM User u WHERE u.role = 'HR' AND u.isActive = true")
    List<User> findAllActiveHR();
    
    /**
     * 👑 หา SuperAdmin ทั้งหมด
     * 
     * ✅ TESTED: System initialization
     * 
     * @return List<User> - เฉพาะ SUPERADMIN
     */
    @Query("SELECT u FROM User u WHERE u.role = 'SUPERADMIN'")
    List<User> findAllSuperAdmins();
    
    // ========== DEPARTMENT QUERIES ==========
    
    /**
     * 🏢 หา Employee ทั้งหมดในแผนก
     * 
     * ✅ TESTED: HR dashboard - filter by department
     * 
     * @param department Department name (IT, Business, Finance)
     * @return List<User> - Employee ในแผนกนั้น
     */
    @Query("SELECT u FROM User u WHERE u.department = :department " +
           "AND u.role = 'EMPLOYEE' AND u.isActive = true")
    List<User> findEmployeesByDepartment(@Param("department") String department);
    
    /**
     * ⚠️ หา Employee ที่ยังไม่มีแผนก
     * 
     * ✅ TESTED: HR assign department page
     * 
     * @return List<User> - Employee ที่ department = NULL
     */
    @Query("SELECT u FROM User u WHERE u.department IS NULL " +
           "AND u.role = 'EMPLOYEE' AND u.isActive = true")
    List<User> findEmployeesWithoutDepartment();
    
    /**
     * 📊 นับจำนวน Employee ในแต่ละแผนก
     * 
     * ✅ TESTED: Admin dashboard statistics
     * 
     * @return List<Object[]> - [department, count]
     *         ตัวอย่าง: ["IT", 15], ["Business", 20]
     */
    @Query("SELECT u.department, COUNT(u) FROM User u " +
           "WHERE u.role = 'EMPLOYEE' AND u.isActive = true " +
           "GROUP BY u.department")
    List<Object[]> countEmployeesByDepartment();
    
    /**
     * 🏢 หาแผนกทั้งหมดที่มี Employee
     * 
     * ✅ TESTED: Department dropdown
     * 
     * @return List<String> - ["IT", "Business", "Finance"]
     */
    @Query("SELECT DISTINCT u.department FROM User u " +
           "WHERE u.role = 'EMPLOYEE' AND u.department IS NOT NULL " +
           "AND u.isActive = true")
    List<String> findAllDepartments();
    
    // ========== ACTIVE STATUS QUERIES ==========
    
    /**
     * ✅ หา User ทั้งหมดที่ active/inactive
     * 
     * ✅ TESTED: Admin user management
     * 
     * @param isActive true = active, false = deactivated
     * @return List<User>
     */
    List<User> findByIsActive(Boolean isActive);
    
    /**
     * 📊 นับจำนวน User ที่ active
     * 
     * ✅ TESTED: Admin dashboard
     * 
     * @param isActive true = active
     * @return long - จำนวน active users
     */
    long countByIsActive(Boolean isActive);
    
    /**
     * 📊 นับจำนวน Employee ที่ active
     * 
     * ✅ TESTED: HR dashboard
     * 
     * @return long - จำนวน active employees
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'EMPLOYEE' AND u.isActive = true")
    long countActiveEmployees();
    
    // ========== SEARCH QUERIES ==========
    
    /**
     * 🔍 ค้นหา User จากชื่อ (case-insensitive)
     * 
     * ✅ TESTED: Search bar
     * 
     * @param name ชื่อที่ต้องการค้นหา
     * @return List<User> - users ที่ชื่อมี keyword
     */
    @Query("SELECT u FROM User u " +
           "WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "AND u.isActive = true")
    List<User> searchByName(@Param("name") String name);
    
    /**
     * 🔍 ค้นหา Employee จากชื่อหรือแผนก
     * 
     * ✅ TESTED: HR search employee
     * 
     * @param keyword คำค้นหา
     * @return List<User> - employees ที่ชื่อหรือแผนกมี keyword
     */
    @Query("SELECT u FROM User u " +
           "WHERE u.role = 'EMPLOYEE' AND u.isActive = true " +
           "AND (LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(u.department) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<User> searchEmployees(@Param("keyword") String keyword);
    
    // ========== STATISTICS ==========
    
    /**
     * 📊 นับจำนวน User แต่ละ Role
     * 
     * ✅ TESTED: Admin dashboard
     * 
     * @return List<Object[]> - [role, count]
     *         ตัวอย่าง: ["EMPLOYEE", 50], ["HR", 5], ["SUPERADMIN", 1]
     */
    @Query("SELECT u.role, COUNT(u) FROM User u " +
           "WHERE u.isActive = true " +
           "GROUP BY u.role")
    List<Object[]> countUsersByRole();
}