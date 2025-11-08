package come.emotion_checkin_syetem.controller;

import come.emotion_checkin_syetem.entity.Notification;
import come.emotion_checkin_syetem.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    // ✅ ดึงแจ้งเตือนทั้งหมด
    @GetMapping
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    // ✅ ส่งแจ้งเตือนใหม่
    @PostMapping
    public String sendNotification(@RequestBody Notification notification) {
        notificationRepository.save(notification);
        return "ส่งแจ้งเตือนสำเร็จ";
    }

    // ✅ ลบแจ้งเตือน
    @DeleteMapping("/{id}")
    public String deleteNotification(@PathVariable Long id) {
        notificationRepository.deleteById(id);
        return "ลบแจ้งเตือนแล้ว";
    }
}
