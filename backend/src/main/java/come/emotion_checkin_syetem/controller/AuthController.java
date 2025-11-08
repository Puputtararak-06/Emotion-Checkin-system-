package come.emotion_checkin_syetem.controller;

import come.emotion_checkin_syetem.entity.User;
import come.emotion_checkin_syetem.repository.UserRepository;
import come.emotion_checkin_syetem.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthService authService;

    // ✅ REGISTER
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return "อีเมลนี้ถูกใช้งานแล้ว";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) user.setRole(User.Role.EMPLOYEE);
        userRepository.save(user);
        return "สมัครสมาชิกสำเร็จ";
    }

    // ✅ LOGIN
    @PostMapping("/login")
    public Object login(@RequestBody User loginRequest) {
        Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());
        if (optionalUser.isEmpty()) return "อีเมลหรือรหัสผ่านไม่ถูกต้อง";

        User user = optionalUser.get();
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return "อีเมลหรือรหัสผ่านไม่ถูกต้อง";
        }

        String token = authService.generateToken(user.getEmail());
        return new Object() {
            public final String message = "เข้าสู่ระบบสำเร็จ";
            public final String Token = token;
            public final Object userInfo = user;
        };
    }
>>>>>>> 516ee4e (เพิ่ม Controller ทั้งหมด (Auth, Checkin, Employee, HR, Notification, SuperAdmin))
}
