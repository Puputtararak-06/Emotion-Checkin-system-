package come.emotion_checkin_syetem.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.LanguageServiceSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * 📍 LOCATION: src/main/java/com/emotion/checkin/config/GoogleCloudConfig.java
 * 
 * 🤖 GOOGLE CLOUD CONFIGURATION - Google NLP API setup
 * 
 * ✅ Purpose:
 * - Configure Google Cloud credentials
 * - Create LanguageServiceClient bean
 * - Enable sentiment analysis
 * 
 * 🔑 Requirements:
 * 1. Google Cloud Project
 * 2. Enable Natural Language API
 * 3. Create Service Account
 * 4. Download JSON credentials
 * 5. Set path in application.properties
 * 
 * ⚠️ IMPORTANT:
 * - Credentials file ต้องอยู่ใน src/main/resources/
 * - ห้าม commit credentials ไป Git!
 * - เพิ่ม *.json ใน .gitignore
 * 
 * 🐛 DEBUG CHECKLIST:
 * ✅ @Configuration annotation
 * ✅ Google Cloud credentials file exists
 * ✅ Natural Language API enabled
 * ✅ Service account has permissions
 */
@Configuration
public class GoogleCloudConfig {
    
    /**
     * Path to Google Cloud credentials JSON file
     * Set in application.properties:
     * google.application.credentials=src/main/resources/google-credentials.json
     */
    @Value("${google.application.credentials:#{null}}")
    private String credentialsPath;
    
    /**
     * 🤖 Language Service Client Bean
     * 
     * Used by GoogleNLPService for sentiment analysis
     * 
     * @return LanguageServiceClient
     * @throws IOException if credentials file not found
     */
    @Bean
    public LanguageServiceClient languageServiceClient() throws IOException {
        // Check if credentials path is configured
        if (credentialsPath == null || credentialsPath.isEmpty()) {
            throw new IllegalStateException(
                "Google Cloud credentials not configured. " +
                "Set google.application.credentials in application.properties"
            );
        }
        
        // Load credentials from file
        GoogleCredentials credentials = GoogleCredentials.fromStream(
            new FileInputStream(credentialsPath)
        );
        
        // Create LanguageServiceSettings with credentials
        LanguageServiceSettings settings = LanguageServiceSettings.newBuilder()
            .setCredentialsProvider(() -> credentials)
            .build();
        
        // Create and return LanguageServiceClient
        return LanguageServiceClient.create(settings);
    }
    
    /**
     * ⚠️ ALTERNATIVE: Use default credentials
     * 
     * If running on GCP (Cloud Run, App Engine, Compute Engine),
     * can use Application Default Credentials instead of JSON file.
     * 
     * Uncomment this and comment out the above bean:
     */
    /*
    @Bean
    public LanguageServiceClient languageServiceClient() throws IOException {
        return LanguageServiceClient.create();
    }
    */
}
