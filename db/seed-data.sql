USE psychapp;

-- ============================
-- INSERTIONS DE TEST
-- ============================

-- Utilisateurs (passwords are 'password123' encoded with BCrypt)
INSERT INTO users (email, password, role, enabled) VALUES
('patient1@msante.sn', '$2a$10$5R6zfzKLgbOB1GlQJf3K4.2PuZvOUVz3HFDrJ8GkCg5XmP7VyF.qe', 'PATIENT', TRUE),
('patient2@msante.sn', '$2a$10$5R6zfzKLgbOB1GlQJf3K4.2PuZvOUVz3HFDrJ8GkCg5XmP7VyF.qe', 'PATIENT', TRUE),
('doctor1@msante.sn', '$2a$10$5R6zfzKLgbOB1GlQJf3K4.2PuZvOUVz3HFDrJ8GkCg5XmP7VyF.qe', 'DOCTOR', TRUE),
('doctor2@msante.sn', '$2a$10$5R6zfzKLgbOB1GlQJf3K4.2PuZvOUVz3HFDrJ8GkCg5XmP7VyF.qe', 'DOCTOR', TRUE);

-- Patients
INSERT INTO patients (user_id, address, phone, birthDate) VALUES
(1, '123 Rue des Lilas, Dakar', '+221-77-123-4567', '1990-05-10'),
(2, '456 Avenue Bourguiba, Thiès', '+221-77-234-5678', '1985-08-15');

-- Docteurs
INSERT INTO doctors (user_id, specialty, licenseNumber, phone) VALUES
(3, 'Psychologie Clinique', 'LIC-PSY-12345', '+221-77-987-6543'),
(4, 'Psychiatrie', 'LIC-PSY-67890', '+221-77-876-5432');

-- Disponibilités du docteur (créneaux d'1h)
INSERT INTO availabilities (doctor_id, start_time, end_time, status) VALUES
-- Doctor 1 availabilities
(1, '2025-01-20 09:00:00', '2025-01-20 10:00:00', 'AVAILABLE'),
(1, '2025-01-20 10:00:00', '2025-01-20 11:00:00', 'AVAILABLE'),
(1, '2025-01-20 14:00:00', '2025-01-20 15:00:00', 'AVAILABLE'),
(1, '2025-01-21 09:00:00', '2025-01-21 10:00:00', 'AVAILABLE'),
(1, '2025-01-21 10:00:00', '2025-01-21 11:00:00', 'BOOKED'),
-- Doctor 2 availabilities
(2, '2025-01-20 11:00:00', '2025-01-20 12:00:00', 'AVAILABLE'),
(2, '2025-01-20 15:00:00', '2025-01-20 16:00:00', 'AVAILABLE'),
(2, '2025-01-21 14:00:00', '2025-01-21 15:00:00', 'AVAILABLE');

-- Rendez-vous lié au créneau BOOKED
INSERT INTO appointments (patient_id, doctor_id, availability_id, status, notes) VALUES
(1, 1, 5, 'CONFIRMED', 'Séance de suivi psychologique');