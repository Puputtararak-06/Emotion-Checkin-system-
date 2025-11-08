package come.emotion_checkin_syetem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * 📍 LOCATION: src/main/java/com/emotion/checkin/config/WebConfig.java
 * 
 * ⚙️ WEB CONFIGURATION - General Spring Boot configuration
 * 
 * ✅ Beans:
 * - BCryptPasswordEncoder: Password hashing
 * 
 * 🐛 DEBUG CHECKLIST:
 * ✅ @Configuration annotation
 * ✅ @EnableWebMvc (optional)
 * ✅ BCryptPasswordEncoder bean
 */
@Configuration
@EnableWebMvc
public class WebConfig {
    
    /**
     * 🔒 BCrypt Password Encoder
     * 
     * Used for:
     * - Hashing passwords on registration
     * - Verifying passwords on login
     * 
     * Strength: 10 rounds (default)
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
