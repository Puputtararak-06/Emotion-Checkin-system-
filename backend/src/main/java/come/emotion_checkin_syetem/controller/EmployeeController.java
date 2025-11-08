package come.emotion_checkin_syetem.controller;

import come.emotion_checkin_syetem.entity.User;
import come.emotion_checkin_syetem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employee")
@CrossOrigin(origins = "*")
public class EmployeeController {

    @Autowired
    private UserRepository userRepository;

    // ✅ ดึงข้อมูลพนักงานทั้งหมด
    @GetMapping
    public List<User> getAllEmployees() {
        return userRepository.findByRole(User.Role.EMPLOYEE);
    }

    // ✅ ดึงข้อมูลพนักงานรายคน
    @GetMapping("/{id}")
    public Optional<User> getEmployeeById(@PathVariable Long id) {
        return userRepository.findById(id);
    }

    // ✅ ปรับปรุงข้อมูลพนักงาน
    @PutMapping("/{id}")
    public String updateEmployee(@PathVariable Long id, @RequestBody User updatedUser) {
        return userRepository.findById(id).map(user -> {
            user.setDepartment(updatedUser.getDepartment());
            user.setPosition(updatedUser.getPosition());
            userRepository.save(user);
            return "อัปเดตข้อมูลสำเร็จ";
        }).orElse("ไม่พบผู้ใช้");
    }

    // ✅ ลบพนักงาน (soft delete)
    @DeleteMapping("/{id}")
    public String deactivateEmployee(@PathVariable Long id) {
        return userRepository.findById(id).map(user -> {
            user.deactivate();
            userRepository.save(user);
            return "ปิดการใช้งานพนักงานเรียบร้อย";
        }).orElse("ไม่พบผู้ใช้");
    }
}
