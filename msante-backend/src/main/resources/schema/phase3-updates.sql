-- Phase 3 Database Schema Updates
-- This script adds all the new fields and tables needed for Phase 3 features

-- Add new fields to doctors table
ALTER TABLE doctors ADD COLUMN IF NOT EXISTS first_name VARCHAR(100);
ALTER TABLE doctors ADD COLUMN IF NOT EXISTS last_name VARCHAR(100);
ALTER TABLE doctors ADD COLUMN IF NOT EXISTS bio TEXT;
ALTER TABLE doctors ADD COLUMN IF NOT EXISTS education TEXT;
ALTER TABLE doctors ADD COLUMN IF NOT EXISTS experience TEXT;
ALTER TABLE doctors ADD COLUMN IF NOT EXISTS consultation_fee VARCHAR(100);
ALTER TABLE doctors ADD COLUMN IF NOT EXISTS languages VARCHAR(200);
ALTER TABLE doctors ADD COLUMN IF NOT EXISTS working_hours VARCHAR(200);
ALTER TABLE doctors ADD COLUMN IF NOT EXISTS department VARCHAR(100);
ALTER TABLE doctors ADD COLUMN IF NOT EXISTS rating DECIMAL(3,2);
ALTER TABLE doctors ADD COLUMN IF NOT EXISTS total_reviews INTEGER DEFAULT 0;

-- Add new fields to patients table
ALTER TABLE patients ADD COLUMN IF NOT EXISTS first_name VARCHAR(100);
ALTER TABLE patients ADD COLUMN IF NOT EXISTS last_name VARCHAR(100);
ALTER TABLE patients ADD COLUMN IF NOT EXISTS phone VARCHAR(50);
ALTER TABLE patients ADD COLUMN IF NOT EXISTS date_of_birth DATE;
ALTER TABLE patients ADD COLUMN IF NOT EXISTS gender VARCHAR(20);
ALTER TABLE patients ADD COLUMN IF NOT EXISTS blood_type VARCHAR(10);
ALTER TABLE patients ADD COLUMN IF NOT EXISTS emergency_contact VARCHAR(100);
ALTER TABLE patients ADD COLUMN IF NOT EXISTS emergency_phone VARCHAR(50);
ALTER TABLE patients ADD COLUMN IF NOT EXISTS city VARCHAR(100);
ALTER TABLE patients ADD COLUMN IF NOT EXISTS country VARCHAR(100);
ALTER TABLE patients ADD COLUMN IF NOT EXISTS insurance_provider VARCHAR(100);
ALTER TABLE patients ADD COLUMN IF NOT EXISTS insurance_number VARCHAR(100);
ALTER TABLE patients ADD COLUMN IF NOT EXISTS medical_history TEXT;
ALTER TABLE patients ADD COLUMN IF NOT EXISTS preferences TEXT;
ALTER TABLE patients ADD COLUMN IF NOT EXISTS profile_image VARCHAR(255);
ALTER TABLE patients ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE;

-- Add new fields to users table
ALTER TABLE users ADD COLUMN IF NOT EXISTS gender VARCHAR(20);

-- Create new tables for patient collections
CREATE TABLE IF NOT EXISTS patient_allergies (
    patient_id BIGINT NOT NULL,
    allergy VARCHAR(255) NOT NULL,
    PRIMARY KEY (patient_id, allergy),
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS patient_medical_conditions (
    patient_id BIGINT NOT NULL,
    condition_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (patient_id, condition_name),
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS patient_medications (
    patient_id BIGINT NOT NULL,
    medication VARCHAR(255) NOT NULL,
    PRIMARY KEY (patient_id, medication),
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_doctors_specialty ON doctors(specialty);
CREATE INDEX IF NOT EXISTS idx_doctors_department ON doctors(department);
CREATE INDEX IF NOT EXISTS idx_doctors_rating ON doctors(rating);
CREATE INDEX IF NOT EXISTS idx_patients_city ON patients(city);
CREATE INDEX IF NOT EXISTS idx_patients_insurance_provider ON patients(insurance_provider);
CREATE INDEX IF NOT EXISTS idx_appointments_date ON appointments(appointment_time);
CREATE INDEX IF NOT EXISTS idx_appointments_doctor_id ON appointments(doctor_id);
CREATE INDEX IF NOT EXISTS idx_appointments_patient_id ON appointments(patient_id);

-- Add some sample data for testing
INSERT INTO doctors (first_name, last_name, specialty, department, bio, education, experience, consultation_fee, languages, working_hours, rating, total_reviews) 
VALUES 
('Dr. John', 'Smith', 'Cardiology', 'Cardiology', 'Experienced cardiologist with 15 years of practice', 'Harvard Medical School', '15 years', '150', 'English, French', '9 AM - 5 PM', 4.8, 45),
('Dr. Sarah', 'Johnson', 'Dermatology', 'Dermatology', 'Board-certified dermatologist specializing in skin cancer', 'Stanford Medical School', '12 years', '120', 'English, Spanish', '8 AM - 4 PM', 4.9, 38),
('Dr. Michael', 'Brown', 'Orthopedics', 'Orthopedics', 'Orthopedic surgeon with expertise in joint replacement', 'Johns Hopkins Medical School', '18 years', '200', 'English', '7 AM - 6 PM', 4.7, 52)
ON DUPLICATE KEY UPDATE 
first_name = VALUES(first_name),
last_name = VALUES(last_name),
specialty = VALUES(specialty),
department = VALUES(department),
bio = VALUES(bio),
education = VALUES(education),
experience = VALUES(experience),
consultation_fee = VALUES(consultation_fee),
languages = VALUES(languages),
working_hours = VALUES(working_hours),
rating = VALUES(rating),
total_reviews = VALUES(total_reviews);

-- Update existing patients with sample data
UPDATE patients SET 
    first_name = 'Jane',
    last_name = 'Doe',
    phone = '+1234567890',
    date_of_birth = '1990-05-15',
    gender = 'Female',
    blood_type = 'O+',
    emergency_contact = 'John Doe',
    emergency_phone = '+1234567891',
    city = 'New York',
    country = 'USA',
    insurance_provider = 'Blue Cross',
    insurance_number = 'BC123456',
    medical_history = 'No major health issues',
    preferences = 'Morning appointments preferred',
    is_active = TRUE
WHERE id = 1;

-- Insert sample allergies, medical conditions, and medications
INSERT INTO patient_allergies (patient_id, allergy) VALUES (1, 'Penicillin'), (1, 'Peanuts') ON DUPLICATE KEY UPDATE allergy = VALUES(allergy);
INSERT INTO patient_medical_conditions (patient_id, condition_name) VALUES (1, 'Hypertension'), (1, 'Asthma') ON DUPLICATE KEY UPDATE condition_name = VALUES(condition_name);
INSERT INTO patient_medications (patient_id, medication) VALUES (1, 'Lisinopril'), (1, 'Albuterol') ON DUPLICATE KEY UPDATE medication = VALUES(medication);

-- Verify the updates
SELECT 'Phase 3 schema updates completed successfully' as status;
