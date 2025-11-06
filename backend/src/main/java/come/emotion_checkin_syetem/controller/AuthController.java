
package come.emotion_checkin_syetem.controller;

import come.emotion_checkin_syetem.entity.User;
import come.emotion_checkin_syetem.repository.UserRepository;
import come.emotion_checkin_syetem.service.JwtService;
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
    private JwtService jwtService;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return "อีเมลนี้ถูกใช้งานแล้ว";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null || user.getRole().isEmpty()) user.setRole("EMPLOYEE");
        userRepository.save(user);
        return "สมัครสมาชิกสำเร็จ";
    }

    @PostMapping("/login")
    public Object login(@RequestBody User login) {
        Optional<User> optionalUser = userRepository.findByEmail(login.getEmail());
        if (optionalUser.isEmpty()) return "อีเมลหรือรหัสผ่านไม่ถูกต้อง";

        User user = optionalUser.get();
        if (!passwordEncoder.matches(login.getPassword(), user.getPassword()))
            return "อีเมลหรือรหัสผ่านไม่ถูกต้อง";

        String token = jwtService.generateToken(user.getEmail());
        return new Object() {
            public final String message = "เข้าสู่ระบบสำเร็จ";
            public final String token = token;
            public final Object userInfo = user;
        };
    }

