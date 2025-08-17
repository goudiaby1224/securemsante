-- M-Santé Database Schema Creation
-- Multi-tenant healthcare platform with Row Level Security

-- Create UUIDs extension if not exists
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Tenant table for multi-tenancy
CREATE TABLE tenant (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name TEXT NOT NULL,
    country_code CHAR(2) NOT NULL DEFAULT 'SN',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    settings JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ
);

-- User account table with multi-role support
CREATE TABLE user_account (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID REFERENCES tenant(id),
    email VARCHAR(255),
    phone VARCHAR(50),
    password_hash VARCHAR(255),
    role VARCHAR(20) NOT NULL CHECK (role IN ('PATIENT','PRACTITIONER','ADMIN','SUPERADMIN')),
    locale VARCHAR(5) NOT NULL DEFAULT 'fr',
    twofa_enabled BOOLEAN NOT NULL DEFAULT false,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','INACTIVE','SUSPENDED','PENDING_VERIFICATION')),
    last_login_at TIMESTAMPTZ,
    failed_login_attempts INTEGER DEFAULT 0,
    locked_until TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ
);

-- Patient profile table
CREATE TABLE patient_profile (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES user_account(id) ON DELETE CASCADE,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    date_of_birth DATE,
    gender VARCHAR(20) CHECK (gender IN ('MALE','FEMALE','OTHER','PREFER_NOT_TO_SAY')),
    address TEXT,
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(50),
    medical_history TEXT,
    allergies TEXT,
    current_medications TEXT,
    privacy_consent BOOLEAN NOT NULL DEFAULT false,
    marketing_consent BOOLEAN DEFAULT false,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    UNIQUE(user_id)
);

-- Practitioner profile table
CREATE TABLE practitioner_profile (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES user_account(id) ON DELETE CASCADE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    title VARCHAR(20),
    specialty VARCHAR(100) NOT NULL,
    license_number VARCHAR(100),
    clinic_name VARCHAR(200),
    clinic_address TEXT,
    bio TEXT,
    languages_spoken VARCHAR(100),
    consultation_fee_cfa INTEGER,
    video_consultation_enabled BOOLEAN DEFAULT true,
    verification_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (verification_status IN ('PENDING','APPROVED','REJECTED','SUSPENDED')),
    verified_at TIMESTAMPTZ,
    verified_by UUID,
    payout_method VARCHAR(20) CHECK (payout_method IN ('ORANGE_MONEY','WAVE','FREE_MONEY','BANK_TRANSFER')),
    payout_details JSONB,
    rating DECIMAL(3,2),
    total_reviews INTEGER DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    UNIQUE(user_id)
);

-- Availability slots for scheduling
CREATE TABLE availability_slot (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenant(id),
    practitioner_id UUID NOT NULL REFERENCES user_account(id),
    starts_at TIMESTAMPTZ NOT NULL,
    ends_at TIMESTAMPTZ NOT NULL,
    mode VARCHAR(20) NOT NULL CHECK (mode IN ('IN_PERSON','VIDEO','PHONE')),
    price_cfa INTEGER NOT NULL,
    is_booked BOOLEAN NOT NULL DEFAULT false,
    booking_buffer_minutes INTEGER DEFAULT 15,
    notes TEXT,
    recurrence_rule TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ
);

-- Booking table for appointments
CREATE TABLE booking (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenant(id),
    patient_id UUID REFERENCES user_account(id),
    practitioner_id UUID NOT NULL REFERENCES user_account(id),
    slot_id UUID NOT NULL REFERENCES availability_slot(id),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING_PAYMENT' CHECK (status IN ('PENDING_PAYMENT','CONFIRMED','CANCELLED','REFUNDED','NO_SHOW','COMPLETED')),
    patient_name VARCHAR(200),
    patient_phone VARCHAR(50),
    patient_email VARCHAR(255),
    consultation_reason TEXT,
    notes TEXT,
    reminder_sent BOOLEAN DEFAULT false,
    confirmed_at TIMESTAMPTZ,
    cancelled_at TIMESTAMPTZ,
    cancellation_reason TEXT,
    cancelled_by UUID,
    completed_at TIMESTAMPTZ,
    no_show_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    UNIQUE(slot_id)
);

-- Payment table for transactions
CREATE TABLE payment (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    booking_id UUID NOT NULL REFERENCES booking(id),
    method VARCHAR(20) NOT NULL CHECK (method IN ('ORANGE_MONEY','WAVE','FREE_MONEY','CARD','BANK_TRANSFER','CASH')),
    amount_cfa INTEGER NOT NULL,
    commission_cfa INTEGER NOT NULL DEFAULT 0,
    net_amount_cfa INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'INITIATED' CHECK (status IN ('INITIATED','PENDING','SUCCEEDED','FAILED','CANCELLED','REFUNDED','CHARGEBACK')),
    reference VARCHAR(100),
    proof_url TEXT,
    gateway_response JSONB,
    processed_at TIMESTAMPTZ,
    failed_reason TEXT,
    refunded_at TIMESTAMPTZ,
    refund_reason TEXT,
    refund_amount_cfa INTEGER,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    UNIQUE(booking_id)
);

-- Payout table for practitioner payments
CREATE TABLE payout (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    practitioner_id UUID NOT NULL REFERENCES user_account(id),
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    amount_net_cfa INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('SCHEDULED','SENT','FAILED')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ
);

-- Message table for internal communications
CREATE TABLE message (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenant(id),
    sender_id UUID NOT NULL REFERENCES user_account(id),
    recipient_id UUID NOT NULL REFERENCES user_account(id),
    body TEXT NOT NULL,
    attachments JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ
);

-- Indexes for performance
CREATE INDEX idx_user_account_email ON user_account(email);
CREATE INDEX idx_user_account_phone ON user_account(phone);
CREATE INDEX idx_user_account_tenant_role ON user_account(tenant_id, role);
CREATE INDEX idx_availability_slot_practitioner ON availability_slot(practitioner_id);
CREATE INDEX idx_availability_slot_date_range ON availability_slot(starts_at, ends_at);
CREATE INDEX idx_availability_slot_available ON availability_slot(is_booked, starts_at) WHERE is_booked = false;
CREATE INDEX idx_booking_patient ON booking(patient_id);
CREATE INDEX idx_booking_practitioner ON booking(practitioner_id);
CREATE INDEX idx_booking_status ON booking(status);
CREATE INDEX idx_booking_guest_phone ON booking(patient_phone) WHERE patient_id IS NULL;
CREATE INDEX idx_payment_booking ON payment(booking_id);
CREATE INDEX idx_message_sender_recipient ON message(sender_id, recipient_id);

-- Row Level Security policies for multi-tenancy
ALTER TABLE user_account ENABLE ROW LEVEL SECURITY;
ALTER TABLE availability_slot ENABLE ROW LEVEL SECURITY;
ALTER TABLE booking ENABLE ROW LEVEL SECURITY;
ALTER TABLE message ENABLE ROW LEVEL SECURITY;

-- RLS policies will be added in a separate migration after testing