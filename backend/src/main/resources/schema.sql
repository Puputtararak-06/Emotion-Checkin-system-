-- ===================================
-- EMOTION CHECK-IN SYSTEM
-- Database: MySQL 8.0+
-- Encoding: UTF-8
-- Timezone: Asia/Bangkok
-- ===================================

-- ===================================
-- 1. EMOTION CATALOG (Pre-defined emotions)
-- ===================================

-- Insert emotion types (15 emotions across 3 levels)

-- LEVEL 3: POSITIVE (😊)
INSERT INTO emotion_catalog (id, name, level, color_code, description, icon, created_at, updated_at) VALUES
(1, 'Happy', 3, '#4CAF50', 'Feeling joyful and content', '😊', NOW(), NOW()),
(2, 'Excited', 3, '#FF9800', 'Full of energy and enthusiasm', '🤩', NOW(), NOW()),
(3, 'Grateful', 3, '#8BC34A', 'Thankful and appreciative', '🙏', NOW(), NOW()),
(4, 'Proud', 3, '#9C27B0', 'Accomplished and satisfied', '😎', NOW(), NOW()),
(5, 'Peaceful', 3, '#00BCD4', 'Calm and relaxed', '😌', NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

-- LEVEL 2: NEUTRAL (😐)
INSERT INTO emotion_catalog (id, name, level, color_code, description, icon, created_at, updated_at) VALUES
(6, 'Okay', 2, '#9E9E9E', 'Neither good nor bad', '😐', NOW(), NOW()),
(7, 'Tired', 2, '#607D8B', 'Feeling exhausted', '😴', NOW(), NOW()),
(8, 'Bored', 2, '#795548', 'Lacking interest or excitement', '😑', NOW(), NOW()),
(9, 'Confused', 2, '#FF5722', 'Uncertain or unclear', '😕', NOW(), NOW()),
(10, 'Indifferent', 2, '#757575', 'No strong feelings either way', '😶', NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

-- LEVEL 1: NEGATIVE (😢)
INSERT INTO emotion_catalog (id, name, level, color_code, description, icon, created_at, updated_at) VALUES
(11, 'Sad', 1, '#2196F3', 'Feeling down or unhappy', '😢', NOW(), NOW()),
(12, 'Stressed', 1, '#F44336', 'Overwhelmed or under pressure', '😰', NOW(), NOW()),
(13, 'Angry', 1, '#E91E63', 'Frustrated or irritated', '😠', NOW(), NOW()),
(14, 'Anxious', 1, '#673AB7', 'Worried or nervous', '😟', NOW(), NOW()),
(15, 'Lonely', 1, '#3F51B5', 'Feeling isolated or alone', '😔', NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

-- ===================================
-- 2. DEFAULT SUPER ADMIN USER
-- ===================================

-- Password: admin123 (BCrypt hashed)
-- Role: SUPERADMIN
-- Use this to login first time

INSERT INTO user (id, name, email, password, role, department, position, is_active, created_at, updated_at) VALUES
(1, 'Super Admin', 'admin@emotion.com', '$2a$10$xJ.qzH3H3fH7wYxX3fH7wOxJ.qzH3H3fH7wYxX3fH7wOxJ.qzH3H3f', 'SUPERADMIN', 'Administration', 'System Administrator', true, NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

-- ⚠️ IMPORTANT: Change this password after first login!
-- Default credentials:
-- Email: admin@emotion.com
-- Password: admin123

-- ===================================
-- 3. DEMO HR USER
-- ===================================

-- Password: hr123 (BCrypt hashed)
-- Role: HR
-- Department: Human Resources

INSERT INTO user (id, name, email, password, role, department, position, is_active, created_at, updated_at) VALUES
(2, 'HR Manager', 'hr@emotion.com', '$2a$10$Hr.qzH3H3fH7wYxX3fH7wOxJ.qzH3H3fH7wYxX3fH7wOxJ.qzH3H3f', 'HR', 'Human Resources', 'HR Manager', true, NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

-- Default credentials:
-- Email: hr@emotion.com
-- Password: hr123

-- ===================================
-- 4. DEMO EMPLOYEE USERS (Optional)
-- ===================================

-- Password: employee123 (BCrypt hashed)

INSERT INTO user (id, name, email, password, role, department, position, is_active, created_at, updated_at) VALUES
(3, 'John Doe', 'john@emotion.com', '$2a$10$Em.qzH3H3fH7wYxX3fH7wOxJ.qzH3H3fH7wYxX3fH7wOxJ.qzH3H3f', 'EMPLOYEE', 'IT', 'Developer', true, NOW(), NOW()),
(4, 'Jane Smith', 'jane@emotion.com', '$2a$10$Em.qzH3H3fH7wYxX3fH7wOxJ.qzH3H3fH7wYxX3fH7wOxJ.qzH3H3f', 'EMPLOYEE', 'IT', 'Designer', true, NOW(), NOW()),
(5, 'Mike Johnson', 'mike@emotion.com', '$2a$10$Em.qzH3H3fH7wYxX3fH7wOxJ.qzH3H3fH7wYxX3fH7wOxJ.qzH3H3f', 'EMPLOYEE', 'Business', 'Analyst', true, NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

-- Default credentials:
-- Email: john@emotion.com / jane@emotion.com / mike@emotion.com
-- Password: employee123

-- ===================================
-- 5. DEMO CHECK-IN DATA (Optional)
-- ===================================

-- Sample check-ins for demo purposes

INSERT INTO emotion_checkin (id, emotion_level, comment, checkin_time, checkin_date, employee_id, emotion_type_id, created_at, updated_at) VALUES
(1, 3, 'Great day! Finished my project ahead of schedule.', '2025-11-06 09:30:00', '2025-11-06', 3, 1, NOW(), NOW()),
(2, 2, 'Feeling a bit tired but okay.', '2025-11-05 10:15:00', '2025-11-05', 3, 7, NOW(), NOW()),
(3, 1, 'Stressed about upcoming deadline.', '2025-11-04 14:20:00', '2025-11-04', 3, 12, NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

-- ===================================
-- 6. INDEXES FOR PERFORMANCE
-- ===================================

-- Add indexes for frequently queried columns

-- User table indexes
CREATE INDEX IF NOT EXISTS idx_user_email ON user(email);
CREATE INDEX IF NOT EXISTS idx_user_role ON user(role);
CREATE INDEX IF NOT EXISTS idx_user_department ON user(department);
CREATE INDEX IF NOT EXISTS idx_user_active ON user(is_active);

-- Emotion check-in indexes
CREATE INDEX IF NOT EXISTS idx_checkin_date ON emotion_checkin(checkin_date);
CREATE INDEX IF NOT EXISTS idx_checkin_employee ON emotion_checkin(employee_id);
CREATE INDEX IF NOT EXISTS idx_checkin_level ON emotion_checkin(emotion_level);
CREATE INDEX IF NOT EXISTS idx_checkin_time ON emotion_checkin(checkin_time);

-- Notification indexes
CREATE INDEX IF NOT EXISTS idx_notification_receiver ON notification(receiver_id);
CREATE INDEX IF NOT EXISTS idx_notification_read ON notification(read_status);
CREATE INDEX IF NOT EXISTS idx_notification_created ON notification(created_at);

-- Audit log indexes
CREATE INDEX IF NOT EXISTS idx_audit_user ON audit_log(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_action ON audit_log(action);
CREATE INDEX IF NOT EXISTS idx_audit_created ON audit_log(created_at);

-- ===================================
-- VERIFICATION QUERIES
-- ===================================

-- Run these to verify setup:

-- Check emotion catalog (should have 15 emotions)
-- SELECT * FROM emotion_catalog ORDER BY level DESC, id;

-- Check users (should have 5 users)
-- SELECT id, name, email, role, department FROM user;

-- Check indexes
-- SHOW INDEXES FROM user;
-- SHOW INDEXES FROM emotion_checkin;

-- ===================================
-- NOTES
-- ===================================

/*
⚠️ SECURITY NOTES:
1. Change default passwords immediately after setup!
2. BCrypt hashes shown are examples - generate real ones:
   https://bcrypt-generator.com/
   
3. Never commit real passwords to Git!

📝 USAGE:
1. Run this script AFTER Spring Boot creates tables
2. Or set spring.jpa.hibernate.ddl-auto=create-drop
3. Or run manually in MySQL Workbench

🔄 RE-RUN:
- Safe to re-run (uses ON DUPLICATE KEY UPDATE)
- Will update existing records
- Won't create duplicates

🎯 DEFAULT CREDENTIALS:
Admin:    admin@emotion.com    / admin123
HR:       hr@emotion.com       / hr123
Employee: john@emotion.com     / employee123
*/