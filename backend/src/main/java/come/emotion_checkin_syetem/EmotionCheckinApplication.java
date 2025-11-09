package come.emotion_checkin_syetem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 📍 LOCATION: src/main/java/com/emotion/checkin/EmotionCheckinApplication.java
 * 
 * 🚀 EMOTION CHECK-IN SYSTEM - MAIN APPLICATION
 * 
 * Spring Boot Application สำหรับระบบ Check-in อารมณ์พนักงาน
 * 
 * Features:
 * - Daily emotion check-in (1 per day)
 * - Google NLP sentiment analysis
 * - Role-based dashboard (Employee, HR, SuperAdmin)
 * - Auto-notification for negative mood
 * - Privacy protection (HR cannot see comments)
 * 
 * Technology Stack:
 * - Spring Boot 3.2.0
 * - MySQL 8.0 / H2 (embedded)
 * - Google Cloud NLP API
 * - Docker + GCP Cloud Run
 * 
 * @author Mae Fah Luang University Team
 * @version 1.0
 * @since 2025-11-07
 */
@SpringBootApplication
@EnableJpaAuditing  // ← สำคัญ! สำหรับ @CreatedDate, @LastModifiedDate ใน BaseEntity
public class EmotionCheckinApplication {

    /**
     * Main method - Entry point of the application
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(EmotionCheckinApplication.class, args);
        
        System.out.println("""
 
            """);
    }
}