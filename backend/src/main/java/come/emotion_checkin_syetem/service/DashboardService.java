package come.emotion_checkin_syetem.service;

import come.emotion_checkin_syetem.dto.response.*;
import come.emotion_checkin_syetem.entity.*;
import come.emotion_checkin_syetem.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 📍 LOCATION: src/main/java/com/emotion/checkin/service/DashboardService.java
 * 
 * 📊 DASHBOARD SERVICE - Role-based dashboard data
 * 
 * ✅ Features:
 * - Employee Dashboard: own check-in history + stats
 * - HR Dashboard: department stats (NO COMMENTS!)
 * - Admin Dashboard: full system overview
 * 
 * ⚠️ CRITICAL - PRIVACY RULES:
 * - Employee: เห็นเฉพาะข้อมูลของตัวเอง (รวม comment)
 * - HR: เห็น department stats แต่ห้ามเห็น raw comments!
 * - SuperAdmin: เห็นทุกอย่าง
 * 
 * 🐛 DEBUG CHECKLIST:
 * ✅ @Service annotation
 * ✅ Role-based filtering
 * ✅ Comment filtering for HR
 * ✅ Thailand timezone
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {
    
    private final UserRepository userRepository;
    private final EmotionCheckinRepository checkinRepository;
    private final NotificationRepository notificationRepository;
    private final AuditLogService auditLogService;
    
    /**
     * 📊 GET EMPLOYEE DASHBOARD
     * 
     * ✅ Employee เห็น:
     * - Check-in history ของตัวเอง (7 days)
     * - Statistics ของตัวเอง
     * - Comment ของตัวเอง
     * - Notifications
     * 
     * @param employeeId Employee ID
     * @return DashboardResponse
     */
    @Transactional(readOnly = true)
    public DashboardResponse getEmployeeDashboard(Long employeeId) {
        log.info("📊 Getting employee dashboard: ID {}", employeeId);
        
        User employee = userRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        // Log activity
        auditLogService.logViewDashboard(employee);
        
        // Get last 7 days check-ins
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Bangkok"));
        LocalDate sevenDaysAgo = today.minusDays(7);
        
        List<EmotionCheckin> checkins = checkinRepository.findByEmployeeAndDateRange(
            employee, sevenDaysAgo, today
        );
        
        // Convert to DTOs (WITH comments - employee can see own comments)
        List<CheckinHistory> history = checkins.stream()
            .map(this::convertToHistoryDTO)
            .collect(Collectors.toList());
        
        // Calculate statistics
        EmotionStats stats = calculateStats(checkins);
        
        // Calculate streak
        int streak = calculateCheckinStreak(employee, today);
        
        // Check if can check-in today
        boolean canCheckinToday = !checkinRepository.existsByEmployeeAndCheckinDate(
            employee, today
        );
        
        // Get unread notifications count
        long unreadNotifications = notificationRepository.countUnreadByReceiver(employee);
        
        return DashboardResponse.builder()
            .userName(employee.getName())
            .userRole(employee.getRole().toString())
            .department(employee.getDepartment())
            .stats(stats)
            .recentCheckins(history)
            .checkinStreak(streak)
            .lastCheckinDate(checkins.isEmpty() ? null : 
                checkins.get(0).getCheckinDate().toString())
            .canCheckinToday(canCheckinToday)
            .unreadNotifications(unreadNotifications)
            .build();
    }
    
    /**
     * 🏢 GET HR DASHBOARD
     * 
     * ✅ HR เห็น:
     * - Department statistics
     * - Employee list (NO COMMENTS!)
     * - Aggregated mood data
     * 
     * ❌ HR ห้ามเห็น:
     * - Raw comments from employees
     * 
     * @param hrId HR user ID
     * @param department Department to view (optional)
     * @return DashboardResponse
     */
    @Transactional(readOnly = true)
    public DashboardResponse getHRDashboard(Long hrId, String department) {
        log.info("🏢 Getting HR dashboard: ID {}, dept {}", hrId, department);
        
        User hr = userRepository.findById(hrId)
            .orElseThrow(() -> new RuntimeException("HR not found"));
        
        if (!hr.isHR() && !hr.isSuperAdmin()) {
            throw new RuntimeException("Access denied: HR role required");
        }
        
        // Log activity
        auditLogService.logViewDashboard(hr);
        
        // Get departments
        List<String> departments = department != null ? 
            List.of(department) : 
            userRepository.findAllDepartments();
        
        // Get department statistics (last 30 days)
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Bangkok"));
        LocalDate thirtyDaysAgo = today.minusDays(30);
        
        List<DepartmentStats> departmentStats = new ArrayList<>();
        
        for (String dept : departments) {
            DepartmentStats stats = getDepartmentStats(dept, thirtyDaysAgo, today);
            departmentStats.add(stats);
        }
        
        // Get employee insights (NO COMMENTS!)
        List<EmployeeInsight> employeeInsights = getEmployeeInsights(
            department, 
            false  // ⚠️ HR ห้ามเห็น comments!
        );
        
        // Count high-risk employees
        int highRiskCount = (int) employeeInsights.stream()
            .filter(EmployeeInsight::getIsHighRisk)
            .count();
        
        return DashboardResponse.builder()
            .userName(hr.getName())
            .userRole(hr.getRole().toString())
            .departments(departments)
            .departmentStats(departmentStats)
            .employeeInsights(employeeInsights)
            .highRiskEmployees(highRiskCount)
            .build();
    }
    
    /**
     * 👑 GET ADMIN DASHBOARD
     * 
     * ✅ SuperAdmin เห็น:
     * - ทุกอย่าง!
     * - รวม raw comments
     * - All departments
     * - System-wide statistics
     * 
     * @param adminId Admin user ID
     * @return DashboardResponse
     */
    @Transactional(readOnly = true)
    public DashboardResponse getAdminDashboard(Long adminId) {
        log.info("👑 Getting admin dashboard: ID {}", adminId);
        
        User admin = userRepository.findById(adminId)
            .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        if (!admin.isSuperAdmin()) {
            throw new RuntimeException("Access denied: SuperAdmin role required");
        }
        
        // Log activity
        auditLogService.logViewDashboard(admin);
        
        // Get all departments
        List<String> departments = userRepository.findAllDepartments();
        
        // Get department statistics (last 30 days)
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Bangkok"));
        LocalDate thirtyDaysAgo = today.minusDays(30);
        
        List<DepartmentStats> departmentStats = new ArrayList<>();
        
        for (String dept : departments) {
            DepartmentStats stats = getDepartmentStats(dept, thirtyDaysAgo, today);
            departmentStats.add(stats);
        }
        
        // Get employee insights (WITH COMMENTS - Admin can see)
        List<EmployeeInsight> employeeInsights = getEmployeeInsights(
            null,  // All departments
            true   // ✅ Admin เห็น comments!
        );
        
        // System-wide statistics
        long totalEmployees = userRepository.countActiveEmployees();
        long todayCheckins = checkinRepository.countTodayCheckins(today);
        
        // High-risk counts
        int highRiskCount = (int) employeeInsights.stream()
            .filter(EmployeeInsight::getIsHighRisk)
            .count();
        
        int consecutiveBadMoodCount = (int) employeeInsights.stream()
            .filter(e -> e.getConsecutiveBadDays() >= 3)
            .count();
        
        return DashboardResponse.builder()
            .userName(admin.getName())
            .userRole(admin.getRole().toString())
            .departments(departments)
            .departmentStats(departmentStats)
            .employeeInsights(employeeInsights)
            .highRiskEmployees(highRiskCount)
            .consecutiveBadMoodCount(consecutiveBadMoodCount)
            .build();
    }
    
    /**
     * 🏢 GET DEPARTMENT STATISTICS
     * 
     * @param department Department name
     * @param startDate Start date
     * @param endDate End date
     * @return DepartmentStatsDTO
     */
    private DepartmentStats getDepartmentStats(
        String department, 
        LocalDate startDate, 
        LocalDate endDate
    ) {
        // Get employees in department
        List<User> employees = userRepository.findEmployeesByDepartment(department);
        long totalEmployees = employees.size();
        long activeEmployees = employees.stream()
            .filter(User::getIsActive)
            .count();
        
        // Get check-ins
        List<EmotionCheckin> checkins = checkinRepository.findByDepartmentAndDateRange(
            department, startDate, endDate
        );
        
        // Count by emotion level
        long positiveCount = checkins.stream()
            .filter(c -> c.getEmotionLevel() == 3)
            .count();
        long neutralCount = checkins.stream()
            .filter(c -> c.getEmotionLevel() == 2)
            .count();
        long negativeCount = checkins.stream()
            .filter(c -> c.getEmotionLevel() == 1)
            .count();
        
        // Calculate average mood score
        double avgMoodScore = checkins.isEmpty() ? 0.0 : 
            checkins.stream()
                .mapToInt(EmotionCheckin::getEmotionLevel)
                .average()
                .orElse(0.0);
        
        // Check-in rate (today)
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Bangkok"));
        long todayCheckins = checkinRepository.findTodayByDepartment(department, today).size();
        double checkinRate = totalEmployees == 0 ? 0.0 : 
            (todayCheckins * 100.0 / totalEmployees);
        
        return DepartmentStats.builder()
            .department(department)
            .totalEmployees(totalEmployees)
            .activeEmployees(activeEmployees)
            .inactiveEmployees(totalEmployees - activeEmployees)
            .checkinRate(checkinRate)
            .todayCheckins(todayCheckins)
            .weeklyCheckins((long) checkins.size())
            .monthlyCheckins((long) checkins.size())
            .positiveCount(positiveCount)
            .neutralCount(neutralCount)
            .negativeCount(negativeCount)
            .averageMoodScore(avgMoodScore)
            .build();
    }
    
    /**
     * 👥 GET EMPLOYEE INSIGHTS
     * 
     * ⚠️ PRIVACY: includeComments flag controls comment visibility
     * - true: Admin can see comments
     * - false: HR cannot see comments
     * 
     * @param department Department filter (null = all)
     * @param includeComments true = include comments (Admin only)
     * @return List<EmployeeInsight>
     */
    private List<EmployeeInsight> getEmployeeInsights(
        String department, 
        boolean includeComments
    ) {
        // Get employees
        List<User> employees = department != null ?
            userRepository.findEmployeesByDepartment(department) :
            userRepository.findAllActiveEmployees();
        
        // Calculate insights for each employee
        return employees.stream()
            .map(emp -> getEmployeeInsight(emp, includeComments))
            .collect(Collectors.toList());
    }
    
    /**
     * 👤 GET EMPLOYEE INSIGHT (Single)
     * 
     * @param employee Employee entity
     * @param includeComment true = include comment (Admin only)
     * @return EmployeeInsight
     */
    private EmployeeInsight getEmployeeInsight(User employee, boolean includeComment) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Bangkok"));
        LocalDate sevenDaysAgo = today.minusDays(7);
        LocalDate thirtyDaysAgo = today.minusDays(30);
        
        // Get last check-in
        var lastCheckin = checkinRepository
            .findFirstByEmployeeOrderByCheckinTimeDesc(employee);
        
        // Get 7-day stats
        List<EmotionCheckin> weeklyCheckins = checkinRepository
            .findByEmployeeAndDateRange(employee, sevenDaysAgo, today);
        
        long weeklyPositive = weeklyCheckins.stream()
            .filter(c -> c.getEmotionLevel() == 3).count();
        long weeklyNeutral = weeklyCheckins.stream()
            .filter(c -> c.getEmotionLevel() == 2).count();
        long weeklyNegative = weeklyCheckins.stream()
            .filter(c -> c.getEmotionLevel() == 1).count();
        
        // Get 30-day stats
        List<EmotionCheckin> monthlyCheckins = checkinRepository
            .findByEmployeeAndDateRange(employee, thirtyDaysAgo, today);
        
        long monthlyPositive = monthlyCheckins.stream()
            .filter(c -> c.getEmotionLevel() == 3).count();
        long monthlyNeutral = monthlyCheckins.stream()
            .filter(c -> c.getEmotionLevel() == 2).count();
        long monthlyNegative = monthlyCheckins.stream()
            .filter(c -> c.getEmotionLevel() == 1).count();
        
        // Calculate streak
        int streak = calculateCheckinStreak(employee, today);
        
        // Check-in rate (last 30 days)
        double checkinRate = (monthlyCheckins.size() * 100.0) / 30.0;
        
        // Average sentiment (if has AI results)
        double avgSentiment = 0.0;
        // TODO: Calculate from EmotionAIResult
        
        // Recent comment (⚠️ PRIVACY!)
        String recentComment = null;
        boolean hasComment = false;
        
        if (includeComment && lastCheckin.isPresent()) {
            recentComment = lastCheckin.get().getComment();
            hasComment = recentComment != null && !recentComment.isEmpty();
        }
        
        return EmployeeInsight.builder()
            .employeeId(employee.getId())
            .name(employee.getName())
            .email(employee.getEmail())
            .department(employee.getDepartment())
            .position(employee.getPosition())
            .isActive(employee.getIsActive())
            .lastCheckin(lastCheckin.map(EmotionCheckin::getCheckinDate).orElse(null))
            .lastMood(lastCheckin.map(c -> c.getEmotionType().getName()).orElse(null))
            .lastMoodLevel(lastCheckin.map(EmotionCheckin::getEmotionLevel).orElse(null))
            .lastMoodEmoji(lastCheckin.map(c -> 
                getEmojiForLevel(c.getEmotionLevel())).orElse(null))
            .checkinStreak(streak)
            .checkinRate(checkinRate)
            .weeklyPositive(weeklyPositive)
            .weeklyNeutral(weeklyNeutral)
            .weeklyNegative(weeklyNegative)
            .monthlyPositive(monthlyPositive)
            .monthlyNeutral(monthlyNeutral)
            .monthlyNegative(monthlyNegative)
            .averageSentiment(avgSentiment)
            .isHighRisk(weeklyNegative >= 3)  // 3+ bad moods in 7 days
            .consecutiveBadDays(0)  // TODO: Calculate
            .recentComment(recentComment)  // ⚠️ NULL for HR!
            .hasComment(hasComment)
            .build();
    }
    
    /**
     * 📊 CALCULATE STATISTICS
     * 
     * @param checkins List of check-ins
     * @return EmotionStats
     */
    private EmotionStats calculateStats(List<EmotionCheckin> checkins) {
        long total = checkins.size();
        
        if (total == 0) {
            return EmotionStats.builder()
                .totalCheckins(0L)
                .positiveCount(0L)
                .neutralCount(0L)
                .negativeCount(0L)
                .positivePercentage(0.0)
                .neutralPercentage(0.0)
                .negativePercentage(0.0)
                .averageSentimentScore(0.0)
                .moodDistribution(new HashMap<>())
                .build();
        }
        
        // Count by level
        long positive = checkins.stream().filter(c -> c.getEmotionLevel() == 3).count();
        long neutral = checkins.stream().filter(c -> c.getEmotionLevel() == 2).count();
        long negative = checkins.stream().filter(c -> c.getEmotionLevel() == 1).count();
        
        // Calculate percentages
        double posPercent = (positive * 100.0) / total;
        double neuPercent = (neutral * 100.0) / total;
        double negPercent = (negative * 100.0) / total;
        
        // Mood distribution
        Map<String, Long> distribution = checkins.stream()
            .collect(Collectors.groupingBy(
                c -> c.getEmotionType().getName(),
                Collectors.counting()
            ));
        
        return EmotionStats.builder()
            .totalCheckins(total)
            .positiveCount(positive)
            .neutralCount(neutral)
            .negativeCount(negative)
            .positivePercentage(posPercent)
            .neutralPercentage(neuPercent)
            .negativePercentage(negPercent)
            .averageSentimentScore(0.0)  // TODO: Calculate from AI results
            .moodDistribution(distribution)
            .build();
    }
    
    /**
     * 🔥 CALCULATE CHECK-IN STREAK
     * 
     * @param employee Employee entity
     * @param today Today's date
     * @return Number of consecutive check-in days
     */
    private int calculateCheckinStreak(User employee, LocalDate today) {
        int streak = 0;
        LocalDate checkDate = today;
        
        // Check backwards from today
        while (true) {
            if (checkinRepository.existsByEmployeeAndCheckinDate(employee, checkDate)) {
                streak++;
                checkDate = checkDate.minusDays(1);
            } else {
                break;
            }
            
            // Limit to 30 days
            if (streak >= 30) break;
        }
        
        return streak;
    }
    
    /**
     * 😊 CONVERT TO HISTORY DTO
     * 
     * @param checkin EmotionCheckin entity
     * @return CheckinHistoryDTO
     */
    private CheckinHistory convertToHistoryDTO(EmotionCheckin checkin) {
        return CheckinHistory.builder()
            .id(checkin.getId())
            .date(checkin.getCheckinDate())
            .emoji(getEmojiForLevel(checkin.getEmotionLevel()))
            .mood(checkin.getEmotionType().getName())
            .level(checkin.getEmotionLevel())
            .colorCode(checkin.getEmotionType().getColorCode())
            .hasComment(checkin.getComment() != null && !checkin.getComment().isEmpty())
            .comment(checkin.getComment())  // Will be filtered by caller if needed
            .checkinTime(checkin.getCheckinTime())
            .timeAgo(calculateTimeAgo(checkin.getCheckinTime()))
            .build();
    }
    
    /**
     * 😊 GET EMOJI FOR LEVEL
     */
    private String getEmojiForLevel(Integer level) {
        return switch (level) {
            case 1 -> "😢";
            case 2 -> "😐";
            case 3 -> "😊";
            default -> "❓";
        };
    }
    
    /**
     * ⏰ CALCULATE TIME AGO
     */
    private String calculateTimeAgo(java.time.LocalDateTime dateTime) {
        java.time.Duration duration = java.time.Duration.between(
            dateTime, 
            java.time.LocalDateTime.now(ZoneId.of("Asia/Bangkok"))
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