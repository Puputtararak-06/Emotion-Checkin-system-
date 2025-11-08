package come.emotion_checkin_syetem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")  // อนุญาตทุก Endpoints ที่ขึ้นต้นด้วย /api/
                .allowedOrigins("http://localhost:5500", "http://127.0.0.1:5500") // ⭐️ (อนุญาตหน้าบ้านเรา)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
