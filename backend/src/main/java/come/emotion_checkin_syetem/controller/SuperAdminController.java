package come.emotion_checkin_syetem.controller;

import come.emotion_checkin_syetem.entity.User;
import come.emotion_checkin_syetem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class SuperAdminController {

    @Autowired
    private UserRepository userRepository;

    // ✅ ดู user ทั้งหมด
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ✅ ดู user รายบุคคล
    @GetMapping("/users/{id}")
    public Optional<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id);
    }

    // ✅ ลบ user ออกจากระบบ
    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "ลบผู้ใช้เรียบร้อย";
    }

    // ✅ เปลี่ยนสถานะ Active/Inactive
    @PutMapping("/users/{id}/toggle")
    public String toggleActive(@PathVariable Long id) {
        return userRepository.findById(id).map(user -> {
            user.setIsActive(!user.getIsActive());
            userRepository.save(user);
            return "เปลี่ยนสถานะผู้ใช้เรียบร้อย";
        }).orElse("ไม่พบผู้ใช้");
    }

    // ✅ สรุปจำนวนผู้ใช้ในแต่ละ Role
    @GetMapping("/stats/roles")
    public List<Object[]> countUsersByRole() {
        return userRepository.countUsersByRole();
    }
}
