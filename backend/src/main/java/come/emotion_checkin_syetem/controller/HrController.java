package come.emotion_checkin_syetem.controller;

import come.emotion_checkin_syetem.entity.User;
import come.emotion_checkin_syetem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/hr")
@CrossOrigin(origins = "*")
public class HrController {

    @Autowired
    private UserRepository userRepository;

    // ✅ ดูรายชื่อพนักงานทั้งหมดในแผนก
    @GetMapping("/department/{department}")
    public List<User> getEmployeesByDepartment(@PathVariable String department) {
        return userRepository.findEmployeesByDepartment(department);
    }

    // ✅ พนักงานที่ยังไม่มีแผนก
    @GetMapping("/unassigned")
    public List<User> getUnassignedEmployees() {
        return userRepository.findEmployeesWithoutDepartment();
    }

    // ✅ มอบหมายแผนกให้พนักงาน
    @PutMapping("/assign/{id}")
    public String assignDepartment(@PathVariable Long id, @RequestParam String department) {
        return userRepository.findById(id).map(user -> {
            user.setDepartment(department);
            userRepository.save(user);
            return "มอบหมายแผนกสำเร็จ";
        }).orElse("ไม่พบพนักงาน");
    }

    // ✅ ดึงสถิติแผนก
    @GetMapping("/stats")
    public List<Object[]> countByDepartment() {
        return userRepository.countEmployeesByDepartment();
    }
}
