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