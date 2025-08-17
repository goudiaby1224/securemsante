CREATE DATABASE IF NOT EXISTS psychapp CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE psychapp;
-- ============================
-- Table users
-- ============================
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('PATIENT', 'DOCTOR') NOT NULL,
    enabled BOOLEAN DEFAULT TRUE
);
-- ============================
-- Table patients
-- ============================
CREATE TABLE patients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    address VARCHAR(255),
    phone VARCHAR(50),
    birthDate DATE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
-- ============================
-- Table doctors
-- ============================
CREATE TABLE doctors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    specialty VARCHAR(255),
    licenseNumber VARCHAR(100),
    phone VARCHAR(50),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
-- ============================
-- Table availabilities (créneaux de 1h)
-- ============================
CREATE TABLE availabilities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    doctor_id BIGINT NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    status ENUM('AVAILABLE','BOOKED','CANCELLED') DEFAULT 'AVAILABLE',
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
);
-- ============================
-- Table appointments
-- ============================
CREATE TABLE appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    availability_id BIGINT NOT NULL UNIQUE,
    status ENUM('PENDING','CONFIRMED','CANCELLED','COMPLETED') DEFAULT 'PENDING',
    notes TEXT,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE,
    FOREIGN KEY (availability_id) REFERENCES availabilities(id) ON DELETE CASCADE
);
-- ============================
-- INSERTIONS DE TEST
-- ============================
-- Utilisateurs
INSERT INTO users (email, password, role, enabled) VALUES
('patient1@example.com', '$2a$10$hashDuMotDePasse', 'PATIENT', TRUE),
('doctor1@example.com', '$2a$10$hashDuMotDePasse', 'DOCTOR', TRUE);
-- Patient
INSERT INTO patients (user_id, address, phone, birthDate) VALUES
(1, '123 Rue des Lilas, Montréal', '514-123-4567', '1990-05-10');
-- Docteur
INSERT INTO doctors (user_id, specialty, licenseNumber, phone) VALUES
(2, 'Psychologie Clinique', 'LIC-12345', '514-987-6543');
-- Disponibilités du docteur (créneaux d’1h)
INSERT INTO availabilities (doctor_id, start_time, end_time, status) VALUES
(1, '2025-08-20 10:00:00', '2025-08-20 11:00:00', 'AVAILABLE'),
(1, '2025-08-20 11:00:00', '2025-08-20 12:00:00', 'AVAILABLE'),
(1, '2025-08-27 14:00:00', '2025-08-27 15:00:00', 'BOOKED');
-- Rendez-vous lié au créneau BOOKED
INSERT INTO appointments (patient_id, doctor_id, availability_id, status, notes) VALUES
(1, 1, 3, 'CONFIRMED', 'Séance de suivi');

--2. Réserver un créneau

 
 