package come.emotion_checkin_syetem.service;

import come.emotion_checkin_syetem.dto.request.SendNotificationRequest;
import come.emotion_checkin_syetem.dto.response.NotificationDTO;  // เปลี่ยนชื่อให้ชัดเจน
import come.emotion_checkin_syetem.entity.Notification;
import come.emotion_checkin_syetem.entity.EmotionCheckin;
import come.emotion_checkin_syetem.entity.User;
import come.emotion_checkin_syetem.repository.NotificationRepository;
import come.emotion_checkin_syetem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 📍 LOCATION: src/main/java/com/emotion/checkin/service/NotificationService.java
 * 
 * 🔔 NOTIFICATION SERVICE - ระบบแจ้งเตือน
 * 
 * ✅ Features:
 * - Auto-notify HR when employee has bad mood
 * - HR send notification to employee
 * - Mark as read (single/bulk)
 * - Get notifications list
 * 
 * 📋 Notification Types:
 * 1. System → HR: Employee มี bad mood (auto)
 * 2. HR → Employee: HR ส่งข้อความ (manual)
 * 
 * 🐛 DEBUG CHECKLIST:
 * ✅ @Service annotation
 * ✅ @Transactional
 * ✅ Thailand timezone
 * ✅ Time elapsed calculation
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    
    /**
     * ⚠️ NOTIFY HR - BAD MOOD (Auto-triggered)
     * 
     * ระบบเรียกอัตโนมัติเมื่อ employee check-in level 1 (Negative)
     * 
     * @param employee Employee with bad mood
     * @param checkin The check-in record
     */
    @Transactional
    public void notifyHRBadMood(User employee, EmotionCheckin checkin) {
        log.warn("⚠️ Notifying HR: Employee {} has bad mood", employee.getName());
        
        // Get all active HRs
        List<User> hrs = userRepository.findAllActiveHR();
        
        if (hrs.isEmpty()) {
            log.warn("⚠️ No active HR found to notify");
            return;
        }
        
        // Create notification for each HR
        String message = String.format(
            "Employee %s reported a negative mood. Please follow up.",
            employee.getName()
        );
        
        for (User hr : hrs) {
            Notification notification = Notification.builder()
                .sender(employee)  // From employee
                .receiver(hr)      // To HR
                .message(message)
                .readStatus(false)
                .relatedCheckin(checkin)
                .build();
            
            notificationRepository.save(notification);
            log.info("✅ Notification sent to HR: {}", hr.getName());
        }
    }
    
    /**
     * 💬 SEND NOTIFICATION (Manual)
     * 
     * HR ส่งข้อความหา Employee
     * 
     * @param senderId HR user ID
     * @param request SendNotificationRequest
     * @throws RuntimeException if validation fails
     */
    @Transactional
    public void sendNotification(Long senderId, SendNotificationRequest request) {
        log.info("💬 Sending notification from user {} to user {}", 
            senderId, request.getReceiverId());
        
        // Validate sender (must be HR or Admin)
        User sender = userRepository.findById(senderId)
            .orElseThrow(() -> new RuntimeException("Sender not found"));
        
        if (!sender.isHR() && !sender.isSuperAdmin()) {
            throw new RuntimeException("Only HR/Admin can send notifications");
        }
        
        // Validate receiver
        User receiver = userRepository.findById(request.getReceiverId())
            .orElseThrow(() -> new RuntimeException("Receiver not found"));
        
        // Create notification
        Notification notification = Notification.builder()
            .sender(sender)
            .receiver(receiver)
            .message(request.getMessage())
            .readStatus(false)
            .build();
        
        // Link to check-in if provided
        if (request.getRelatedCheckinId() != null) {
            // TODO: Set relatedCheckin
        }
        
        notificationRepository.save(notification);
        
        // Log activity
        auditLogService.logSendNotification(sender, receiver);
        
        log.info("✅ Notification sent successfully");
    }
    
    /**
     * 📬 GET NOTIFICATIONS
     * 
     * @param userId User ID (receiver)
     * @return List<NotificationDTO>
     */
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotifications(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Notification> notifications = notificationRepository.findByReceiver(user);
        
        return notifications.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * 🔴 GET UNREAD NOTIFICATIONS
     * 
     * @param userId User ID (receiver)
     * @return List<NotificationDTO>
     */
    @Transactional(readOnly = true)
    public List<NotificationDTO> getUnreadNotifications(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Notification> notifications = notificationRepository.findUnreadByReceiver(user);
        
        return notifications.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * 🔢 COUNT UNREAD NOTIFICATIONS
     * 
     * @param userId User ID (receiver)
     * @return Unread count
     */
    @Transactional(readOnly = true)
    public long countUnreadNotifications(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return notificationRepository.countUnreadByReceiver(user);
    }
    
    /**
     * ✅ MARK AS READ (Single)
     * 
     * @param notificationId Notification ID
     */
    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.markAsRead(notificationId);
        log.info("✅ Notification {} marked as read", notificationId);
    }
    
    /**
     * ✅ MARK ALL AS READ
     * 
     * @param userId User ID (receiver)
     */
    @Transactional
    public void markAllAsRead(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        notificationRepository.markAllAsRead(user);
        log.info("✅ All notifications marked as read for user {}", userId);
    }
    
    /**
     * 🗑️ DELETE OLD NOTIFICATIONS (Cleanup)
     * 
     * ลบ notifications ที่อ่านแล้วและเก่ากว่า 30 วัน
     * 
     * @param daysOld จำนวนวันเก่า (default = 30)
     */
    @Transactional
    public void cleanupOldNotifications(int daysOld) {
        LocalDateTime cutoffDate = LocalDateTime.now(ZoneId.of("Asia/Bangkok"))
            .minusDays(daysOld);
        
        notificationRepository.deleteOldReadNotifications(cutoffDate);
        log.info("🗑️ Deleted notifications older than {} days", daysOld);
    }
    
    /**
     * 🔄 CONVERT TO DTO
     * 
     * @param notification Notification entity
     * @return NotificationDTO
     */
    private NotificationDTO convertToDTO(Notification notification) {
        return NotificationDTO.builder()
            .id(notification.getId())
            .message(notification.getMessage())
            .senderName(notification.getSender().getName())
            .senderRole(notification.getSender().getRole().toString())
            .readStatus(notification.getReadStatus())
            .createdAt(notification.getCreatedAt())
            .timeAgo(calculateTimeAgo(notification.getCreatedAt()))
            .relatedCheckinId(notification.getRelatedCheckin() != null ? 
                notification.getRelatedCheckin().getId() : null)
            .type(determineType(notification))
            .priority(determinePriority(notification))
            .build();
    }
    
    /**
     * 🏷️ DETERMINE TYPE
     */
    private String determineType(Notification notification) {
        if (notification.getRelatedCheckin() != null && 
            notification.getRelatedCheckin().isBadMood()) {
            return "ALERT";  // System alert for bad mood
        }
        if (notification.getSender().isHR()) {
            return "MESSAGE";  // HR message
        }
        return "SYSTEM";
    }
    
    /**
     * 🔴 DETERMINE PRIORITY
     */
    private String determinePriority(Notification notification) {
        if (notification.getRelatedCheckin() != null && 
            notification.getRelatedCheckin().isBadMood()) {
            return "HIGH";  // Bad mood = high priority
        }
        return "NORMAL";
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