-- User Table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

-- Doctor Table
CREATE TABLE doctors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    specialty VARCHAR(255),
    license_number VARCHAR(100),
    phone VARCHAR(50),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Patient Table
CREATE TABLE patients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    address VARCHAR(255),
    phone VARCHAR(50),
    birth_date DATE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Availability Table
CREATE TABLE availabilities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    doctor_id BIGINT NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    UNIQUE KEY uq_av_doc_start (doctor_id, start_time),
    INDEX idx_av_doc_time (doctor_id, start_time),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id)
);

-- Appointment Table
CREATE TABLE appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    availability_id BIGINT NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL,
    mode VARCHAR(50),
    notes TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_ap_patient (patient_id),
    INDEX idx_ap_status (status),
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id),
    FOREIGN KEY (availability_id) REFERENCES availabilities(id)
);

-- Sample INSERTs
INSERT INTO users (email, password, role, enabled, created_at, updated_at) VALUES
('doctor@example.com', '$2a$12$JKBsLzjVnfDkg0O54MdlceTpb2DzNRFv011i05tRvG5YCXYyR1eLC', 'DOCTOR', TRUE, NOW(), NOW()),
('patient@example.com', '$2a$12$qdd41JLcvMyV6AQ5t4X61ejH7qHBUP68p4g4ex/wSc8xgvOgl95D2', 'PATIENT', TRUE, NOW(), NOW());

INSERT INTO doctors (user_id, specialty, license_number, phone, created_at, updated_at) VALUES
(1, 'Cardiology', 'LIC123', '1234567890', NOW(), NOW());

INSERT INTO patients (user_id, address, phone, birth_date, created_at, updated_at) VALUES
(2, '123 Main St', '0987654321', '1990-01-01', NOW(), NOW());

INSERT INTO availabilities (doctor_id, start_time, end_time, status, created_at, updated_at) VALUES
(1, '2025-10-17 09:00:00', '2025-10-17 10:00:00', 'AVAILABLE', NOW(), NOW());

INSERT INTO appointments (patient_id, doctor_id, availability_id, status, mode, notes, created_at, updated_at) VALUES
(1, 1,1, 'BOOKED', 'IN_PERSON', 'First appointment', NOW(), NOW());