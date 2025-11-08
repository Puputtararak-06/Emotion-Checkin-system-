package come.emotion_checkin_syetem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 📍 LOCATION: src/main/java/com/emotion/checkin/config/CorsConfig.java
 * 
 * 🌐 CORS CONFIGURATION - Allow frontend to call backend
 * 
 * ✅ Purpose:
 * - Allow frontend (running on different port/domain) to call API
 * - Enable preflight requests (OPTIONS)
 * - Configure allowed methods & headers
 * 
 * 🔧 Configuration:
 * - Development: Allow all origins (*)
 * - Production: Specify frontend URL
 * 
 * 🐛 DEBUG CHECKLIST:
 * ✅ @Configuration annotation
 * ✅ implements WebMvcConfigurer
 * ✅ Allowed origins (change for production!)
 * ✅ Allowed methods (GET, POST, PUT, DELETE)
 * ✅ Allowed headers (Content-Type, X-User-Id, etc.)
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")  // Apply to all /api/* endpoints
            
            // ⚠️ DEVELOPMENT: Allow all origins
            // 🔒 PRODUCTION: Change to specific frontend URL
            .allowedOrigins(
                "http://localhost:3000",      // React/Vue local dev
                "http://localhost:5173",      // Vite local dev
                "http://127.0.0.1:5500",      // Live Server
                "*"                            // All origins (dev only!)
            )
            
            // Allowed HTTP methods
            .allowedMethods(
                "GET", 
                "POST", 
                "PUT", 
                "DELETE", 
                "OPTIONS"
            )
            
            // Allowed headers
            .allowedHeaders(
                "Content-Type",
                "Authorization",
                "X-User-Id",           // Custom header for user ID
                "X-Requested-With"
            )
            // Expose headers (if needed)
            .exposedHeaders(
                "X-Total-Count"
            )
            // Allow credentials (cookies, auth headers)
            .allowCredentials(true)
            // Max age for preflight cache (1 hour)
            .maxAge(3600);
    }
}

