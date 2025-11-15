CREATE DATABASE IF NOT EXISTS emotion_checkin_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
USE emotion_checkin_db;

CREATE TABLE users (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
name VARCHAR(100) NOT NULL,
email VARCHAR(150) NOT NULL UNIQUE,
password VARCHAR(255) NOT NULL,
role VARCHAR(20) NOT NULL, -- EMPLOYEE | HR | SUPERADMIN
department VARCHAR(100), -- assigned by SUPERADMIN only
position VARCHAR(100),
is_active BOOLEAN DEFAULT TRUE,

created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE
CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE emotion_catalog (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
level INT NOT NULL, -- 1=Negative, 2=Neutral, 3=Positive
name VARCHAR(50) NOT NULL, -- e.g. Happy, Sad, Calm
color_code VARCHAR(10) -- optional HEX
) ENGINE=InnoDB;
-- Insert system user
insert into users(name,email,password,role,department,position,is_active)
value ('System','system@internal','N/A' ,'Superadmin' , null ,'system',True);
-- Insert 15 emotion types
INSERT INTO emotion_catalog (level, name, color_code) VALUES
(3, 'Happy', '#4CAF50'),
(3, 'Relaxed', '#4CAF50'),
(3, 'Excited', '#4CAF50'),
(3, 'Proud', '#4CAF50'),
(3, 'Motivated', '#4CAF50'),
(2, 'Calm', '#FFC107'),
(2, 'Tired', '#FFC107'),
(2, 'Indifferent', '#FFC107'),
(2, 'Focused', '#FFC107'),
(2, 'Uncertain', '#FFC107'),
(1, 'Sad', '#F44336'),
(1, 'Angry', '#F44336'),
(1, 'Stressed', '#F44336'),
(1, 'Anxious', '#F44336'),
(1, 'Bored', '#F44336');

INSERT INTO users (name, email, password, role, department, position, is_active)
VALUES ('System', 'system@internal', 'system', 'SUPERADMIN', NULL, 'SYSTEM', TRUE);
CREATE TABLE emotion_checkin (

   
id BIGINT AUTO_INCREMENT PRIMARY KEY,
employee_id BIGINT NOT NULL,
emotion_level INT NOT NULL,
emotion_type_id BIGINT NOT NULL,
comment TEXT,

checkin_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
checkin_date DATE NOT NULL,
CONSTRAINT fk_checkin_employee FOREIGN KEY (employee_id)
REFERENCES users(id) ON DELETE CASCADE,
CONSTRAINT fk_checkin_emotype FOREIGN KEY (emotion_type_id)
REFERENCES emotion_catalog(id),
CONSTRAINT uk_employee_daily UNIQUE (employee_id, checkin_date)
) ENGINE=InnoDB;

CREATE TABLE emotion_ai_result (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
checkin_id BIGINT NOT NULL,
sentiment_score FLOAT,
magnitude FLOAT,
sentiment_label VARCHAR(20), -- POSITIVE | NEUTRAL | NEGATIVE
language VARCHAR(10),
analyzed_at DATETIME DEFAULT CURRENT_TIMESTAMP,
CONSTRAINT fk_ai_checkin FOREIGN KEY (checkin_id)
REFERENCES emotion_checkin(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE notifications (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
sender_id BIGINT,
receiver_id BIGINT NOT NULL,
message TEXT NOT NULL,
read_status BOOLEAN DEFAULT FALSE,
created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
related_checkin_id BIGINT,
priority VARCHAR(20) DEFAULT 'NORMAL', -- HIGH | NORMAL | LOW

type VARCHAR(20) DEFAULT 'MESSAGE', -- SYSTEM | ALERT | MESSAGE
CONSTRAINT fk_noti_sender FOREIGN KEY (sender_id) REFERENCES users(id),
CONSTRAINT fk_noti_receiver FOREIGN KEY (receiver_id) REFERENCES users(id),
CONSTRAINT fk_noti_checkin FOREIGN KEY (related_checkin_id)
REFERENCES emotion_checkin(id)
) ENGINE=InnoDB;

CREATE TABLE audit_log (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
user_id BIGINT ,
action VARCHAR(50) NOT NULL,
target_user_id BIGINT,
details TEXT,
ip_address VARCHAR(50),
is_critical BOOLEAN DEFAULT FALSE,
is_auth_action BOOLEAN DEFAULT FALSE,
created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES users(id),
CONSTRAINT fk_audit_target FOREIGN KEY (target_user_id) REFERENCES users(id)
) ENGINE=InnoDB;
()
