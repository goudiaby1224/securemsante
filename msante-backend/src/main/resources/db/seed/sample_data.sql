-- M-Santé Seed Data for Development and Testing
-- This file creates sample data for testing the platform

-- Insert default tenant
INSERT INTO tenant (id, name, country_code, status) VALUES 
    ('550e8400-e29b-41d4-a716-446655440000', 'Clinique Exemple Dakar', 'SN', 'ACTIVE');

-- Insert sample user accounts
-- Admin user
INSERT INTO user_account (id, tenant_id, email, phone, role, status, locale) VALUES 
    ('550e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440000', 'admin@msante.sn', '+221701234567', 'ADMIN', 'ACTIVE', 'fr');

-- Sample practitioners
INSERT INTO user_account (id, tenant_id, email, phone, role, status, locale) VALUES 
    ('550e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440000', 'dr.diop@msante.sn', '+221771234567', 'PRACTITIONER', 'ACTIVE', 'fr'),
    ('550e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440000', 'dr.ndiaye@msante.sn', '+221781234567', 'PRACTITIONER', 'ACTIVE', 'fr'),
    ('550e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440000', 'dr.fall@msante.sn', '+221791234567', 'PRACTITIONER', 'ACTIVE', 'fr'),
    ('550e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440000', 'psy.sarr@msante.sn', '+221701234568', 'PRACTITIONER', 'ACTIVE', 'fr'),
    ('550e8400-e29b-41d4-a716-446655440006', '550e8400-e29b-41d4-a716-446655440000', 'dr.ba@msante.sn', '+221771234568', 'PRACTITIONER', 'ACTIVE', 'fr');

-- Sample patients
INSERT INTO user_account (id, tenant_id, phone, role, status, locale) VALUES 
    ('550e8400-e29b-41d4-a716-446655440007', '550e8400-e29b-41d4-a716-446655440000', '+221781234568', 'PATIENT', 'ACTIVE', 'fr'),
    ('550e8400-e29b-41d4-a716-446655440008', '550e8400-e29b-41d4-a716-446655440000', '+221791234568', 'PATIENT', 'ACTIVE', 'fr'),
    ('550e8400-e29b-41d4-a716-446655440009', '550e8400-e29b-41d4-a716-446655440000', '+221701234569', 'PATIENT', 'ACTIVE', 'fr');

-- Insert practitioner profiles
INSERT INTO practitioner_profile (id, user_id, first_name, last_name, title, specialty, license_number, clinic_name, clinic_address, bio, languages_spoken, consultation_fee_cfa, verification_status, verified_at, payout_method) VALUES 
    ('650e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440002', 'Amadou', 'Diop', 'Dr.', 'Médecine Générale', 'LIC-001-SN', 'Cabinet Dr. Diop', 'Plateau, Dakar', 'Médecin généraliste avec 10 ans d''expérience', 'fr,wo', 15000, 'APPROVED', now(), 'ORANGE_MONEY'),
    ('650e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440003', 'Fatou', 'Ndiaye', 'Dr.', 'Pédiatrie', 'LIC-002-SN', 'Centre Pédiatrique Ndiaye', 'Almadies, Dakar', 'Spécialiste en pédiatrie et soins infantiles', 'fr,wo,en', 20000, 'APPROVED', now(), 'WAVE'),
    ('650e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440004', 'Moussa', 'Fall', 'Dr.', 'Cardiologie', 'LIC-003-SN', 'Clinique Cardiologique Fall', 'Point E, Dakar', 'Cardiologue interventionnel, spécialiste des maladies cardiovasculaires', 'fr,en', 35000, 'APPROVED', now(), 'FREE_MONEY'),
    ('650e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440005', 'Aïcha', 'Sarr', 'Psy.', 'Psychologie Clinique', 'LIC-004-SN', 'Cabinet Psychologique Sarr', 'Mermoz, Dakar', 'Psychologue clinicienne spécialisée en thérapie cognitive et comportementale', 'fr,wo', 25000, 'APPROVED', now(), 'ORANGE_MONEY'),
    ('650e8400-e29b-41d4-a716-446655440006', '550e8400-e29b-41d4-a716-446655440006', 'Ibrahima', 'Ba', 'Dr.', 'Dermatologie', 'LIC-005-SN', 'Clinique Dermatologique Ba', 'Sacré-Cœur, Dakar', 'Dermatologue et vénérologue', 'fr,wo,en', 30000, 'APPROVED', now(), 'WAVE');

-- Insert patient profiles
INSERT INTO patient_profile (id, user_id, first_name, last_name, date_of_birth, gender, privacy_consent) VALUES 
    ('750e8400-e29b-41d4-a716-446655440007', '550e8400-e29b-41d4-a716-446655440007', 'Khady', 'Sow', '1990-05-15', 'FEMALE', true),
    ('750e8400-e29b-41d4-a716-446655440008', '550e8400-e29b-41d4-a716-446655440008', 'Omar', 'Mbaye', '1985-12-03', 'MALE', true),
    ('750e8400-e29b-41d4-a716-446655440009', '550e8400-e29b-41d4-a716-446655440009', 'Mariama', 'Diallo', '1995-08-22', 'FEMALE', true);

-- Insert availability slots for the next 2 weeks
-- Dr. Diop (General Medicine) - Morning slots
INSERT INTO availability_slot (id, tenant_id, practitioner_id, starts_at, ends_at, mode, price_cfa, is_booked) VALUES 
    ('850e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440002', '2025-08-20 08:00:00+00', '2025-08-20 09:00:00+00', 'IN_PERSON', 15000, false),
    ('850e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440002', '2025-08-20 09:00:00+00', '2025-08-20 10:00:00+00', 'IN_PERSON', 15000, false),
    ('850e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440002', '2025-08-20 10:00:00+00', '2025-08-20 11:00:00+00', 'VIDEO', 15000, false),
    ('850e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440002', '2025-08-20 11:00:00+00', '2025-08-20 12:00:00+00', 'VIDEO', 15000, false);

-- Dr. Ndiaye (Pediatrics) - Afternoon slots
INSERT INTO availability_slot (id, tenant_id, practitioner_id, starts_at, ends_at, mode, price_cfa, is_booked) VALUES 
    ('850e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440003', '2025-08-20 14:00:00+00', '2025-08-20 15:00:00+00', 'IN_PERSON', 20000, false),
    ('850e8400-e29b-41d4-a716-446655440006', '550e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440003', '2025-08-20 15:00:00+00', '2025-08-20 16:00:00+00', 'IN_PERSON', 20000, false),
    ('850e8400-e29b-41d4-a716-446655440007', '550e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440003', '2025-08-20 16:00:00+00', '2025-08-20 17:00:00+00', 'VIDEO', 20000, false);

-- Dr. Fall (Cardiology) - Mixed slots
INSERT INTO availability_slot (id, tenant_id, practitioner_id, starts_at, ends_at, mode, price_cfa, is_booked) VALUES 
    ('850e8400-e29b-41d4-a716-446655440008', '550e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440004', '2025-08-21 09:00:00+00', '2025-08-21 10:00:00+00', 'IN_PERSON', 35000, false),
    ('850e8400-e29b-41d4-a716-446655440009', '550e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440004', '2025-08-21 10:00:00+00', '2025-08-21 11:00:00+00', 'IN_PERSON', 35000, false),
    ('850e8400-e29b-41d4-a716-446655440010', '550e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440004', '2025-08-21 15:00:00+00', '2025-08-21 16:00:00+00', 'VIDEO', 35000, false);

-- Psy. Sarr (Psychology) - Video sessions preferred
INSERT INTO availability_slot (id, tenant_id, practitioner_id, starts_at, ends_at, mode, price_cfa, is_booked) VALUES 
    ('850e8400-e29b-41d4-a716-446655440011', '550e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440005', '2025-08-22 10:00:00+00', '2025-08-22 11:00:00+00', 'VIDEO', 25000, false),
    ('850e8400-e29b-41d4-a716-446655440012', '550e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440005', '2025-08-22 11:00:00+00', '2025-08-22 12:00:00+00', 'VIDEO', 25000, false),
    ('850e8400-e29b-41d4-a716-446655440013', '550e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440005', '2025-08-22 14:00:00+00', '2025-08-22 15:00:00+00', 'IN_PERSON', 25000, false);

-- Dr. Ba (Dermatology) - Mixed availability
INSERT INTO availability_slot (id, tenant_id, practitioner_id, starts_at, ends_at, mode, price_cfa, is_booked) VALUES 
    ('850e8400-e29b-41d4-a716-446655440014', '550e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440006', '2025-08-23 08:30:00+00', '2025-08-23 09:30:00+00', 'IN_PERSON', 30000, false),
    ('850e8400-e29b-41d4-a716-446655440015', '550e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440006', '2025-08-23 09:30:00+00', '2025-08-23 10:30:00+00', 'IN_PERSON', 30000, false),
    ('850e8400-e29b-41d4-a716-446655440016', '550e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440006', '2025-08-23 14:00:00+00', '2025-08-23 15:00:00+00', 'VIDEO', 30000, false);

-- Sample bookings (some confirmed, some pending)
INSERT INTO booking (id, tenant_id, patient_id, practitioner_id, slot_id, status, consultation_reason, confirmed_at) VALUES 
    ('950e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440007', '550e8400-e29b-41d4-a716-446655440002', '850e8400-e29b-41d4-a716-446655440001', 'CONFIRMED', 'Consultation de routine', now()),
    ('950e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440008', '550e8400-e29b-41d4-a716-446655440003', '850e8400-e29b-41d4-a716-446655440005', 'CONFIRMED', 'Suivi pédiatrique', now());

-- Guest booking example
INSERT INTO booking (id, tenant_id, practitioner_id, slot_id, status, patient_name, patient_phone, consultation_reason) VALUES 
    ('950e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440005', '850e8400-e29b-41d4-a716-446655440011', 'PENDING_PAYMENT', 'Abdou Seck', '+221701234570', 'Consultation psychologique');

-- Update booked slots
UPDATE availability_slot SET is_booked = true WHERE id IN (
    '850e8400-e29b-41d4-a716-446655440001',
    '850e8400-e29b-41d4-a716-446655440005',
    '850e8400-e29b-41d4-a716-446655440011'
);

-- Sample payments for confirmed bookings
INSERT INTO payment (id, booking_id, method, amount_cfa, commission_cfa, net_amount_cfa, status, reference, processed_at) VALUES 
    ('a50e8400-e29b-41d4-a716-446655440001', '950e8400-e29b-41d4-a716-446655440001', 'ORANGE_MONEY', 15000, 375, 14625, 'SUCCEEDED', 'OM-2025-001', now()),
    ('a50e8400-e29b-41d4-a716-446655440002', '950e8400-e29b-41d4-a716-446655440002', 'WAVE', 20000, 500, 19500, 'SUCCEEDED', 'WAVE-2025-001', now());