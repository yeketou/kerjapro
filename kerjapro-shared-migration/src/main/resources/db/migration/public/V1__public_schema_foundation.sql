-- ============================================================
-- KerjaPro Public Schema Foundation
-- V1: Core platform tables
-- ============================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Users (mirrors Keycloak, for local reference & search)
CREATE TABLE users (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    keycloak_id   VARCHAR(255) NOT NULL UNIQUE,
    email         VARCHAR(255) NOT NULL UNIQUE,
    role          VARCHAR(50)  NOT NULL CHECK (role IN ('MAIN_CONTRACTOR', 'SUBCONTRACTOR', 'PLATFORM_ADMIN')),
    full_name     VARCHAR(255),
    phone         VARCHAR(30),
    is_active     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ,
    deleted_at    TIMESTAMPTZ,
    version       BIGINT NOT NULL DEFAULT 0
);

-- Subcontractor Profiles
CREATE TABLE subcontractor_profiles (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id               UUID NOT NULL REFERENCES users(id),
    business_name         VARCHAR(255) NOT NULL,
    display_name          VARCHAR(255),
    bio                   TEXT,
    phone                 VARCHAR(30) NOT NULL,
    email                 VARCHAR(255) NOT NULL,
    city                  VARCHAR(100) NOT NULL,
    state                 VARCHAR(100) NOT NULL,
    profile_photo_url     TEXT,
    cidb_grade            VARCHAR(10),
    cidb_registration_no  VARCHAR(100),
    subscription_tier     VARCHAR(20) NOT NULL DEFAULT 'FREE' CHECK (subscription_tier IN ('FREE', 'PRO', 'PREMIUM')),
    is_verified           BOOLEAN NOT NULL DEFAULT FALSE,
    is_available          BOOLEAN NOT NULL DEFAULT TRUE,
    average_rating        NUMERIC(3,2),
    total_reviews         INT NOT NULL DEFAULT 0,
    total_completed_jobs  INT NOT NULL DEFAULT 0,
    created_at            TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by            VARCHAR(255),
    updated_at            TIMESTAMPTZ,
    updated_by            VARCHAR(255),
    deleted_at            TIMESTAMPTZ,
    version               BIGINT NOT NULL DEFAULT 0
);

-- Trade Specializations
CREATE TABLE trade_specializations (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    profile_id       UUID NOT NULL REFERENCES subcontractor_profiles(id) ON DELETE CASCADE,
    trade_category   VARCHAR(50) NOT NULL,
    years_experience INT,
    description      TEXT,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ,
    deleted_at       TIMESTAMPTZ,
    version          BIGINT NOT NULL DEFAULT 0
);

-- Brand Certifications
CREATE TABLE brand_certifications (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    profile_id           UUID NOT NULL REFERENCES subcontractor_profiles(id) ON DELETE CASCADE,
    brand_name           VARCHAR(100) NOT NULL,
    certification_name   VARCHAR(255) NOT NULL,
    certification_no     VARCHAR(100),
    issued_date          DATE,
    expiry_date          DATE,
    certificate_url      TEXT,
    is_verified          BOOLEAN NOT NULL DEFAULT FALSE,
    created_at           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by           VARCHAR(255),
    updated_at           TIMESTAMPTZ,
    updated_by           VARCHAR(255),
    deleted_at           TIMESTAMPTZ,
    version              BIGINT NOT NULL DEFAULT 0
);

-- Portfolio Items
CREATE TABLE portfolio_items (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    profile_id          UUID NOT NULL REFERENCES subcontractor_profiles(id) ON DELETE CASCADE,
    project_title       VARCHAR(255) NOT NULL,
    project_description TEXT,
    location            VARCHAR(255),
    completed_date      DATE,
    brand_used          VARCHAR(100),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(255),
    updated_at          TIMESTAMPTZ,
    updated_by          VARCHAR(255),
    deleted_at          TIMESTAMPTZ,
    version             BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE portfolio_photos (
    portfolio_item_id  UUID NOT NULL REFERENCES portfolio_items(id) ON DELETE CASCADE,
    photo_url          TEXT NOT NULL
);

-- Projects & Work Packages
CREATE TABLE projects (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    main_contractor_id UUID NOT NULL REFERENCES users(id),
    title            VARCHAR(255) NOT NULL,
    description      TEXT,
    location         VARCHAR(255),
    start_date       DATE,
    end_date         DATE,
    status           VARCHAR(30) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'ACTIVE', 'COMPLETED', 'CANCELLED')),
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by       VARCHAR(255),
    updated_at       TIMESTAMPTZ,
    updated_by       VARCHAR(255),
    deleted_at       TIMESTAMPTZ,
    version          BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE work_packages (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id       UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    title            VARCHAR(255) NOT NULL,
    trade_category   VARCHAR(50) NOT NULL,
    description      TEXT,
    required_brand   VARCHAR(100),
    budget_min       NUMERIC(15,2),
    budget_max       NUMERIC(15,2),
    start_date       DATE,
    end_date         DATE,
    status           VARCHAR(30) NOT NULL DEFAULT 'OPEN' CHECK (status IN ('OPEN', 'ASSIGNED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
    assigned_sub_id  UUID REFERENCES subcontractor_profiles(id),
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by       VARCHAR(255),
    updated_at       TIMESTAMPTZ,
    updated_by       VARCHAR(255),
    deleted_at       TIMESTAMPTZ,
    version          BIGINT NOT NULL DEFAULT 0
);

-- Bookings / Appointments
CREATE TABLE bookings (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    work_package_id   UUID REFERENCES work_packages(id),
    main_contractor_id UUID NOT NULL REFERENCES users(id),
    sub_profile_id    UUID NOT NULL REFERENCES subcontractor_profiles(id),
    appointment_at    TIMESTAMPTZ NOT NULL,
    duration_minutes  INT NOT NULL DEFAULT 60,
    location          VARCHAR(255),
    notes             TEXT,
    status            VARCHAR(30) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'CONFIRMED', 'DECLINED', 'COMPLETED', 'CANCELLED')),
    created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by        VARCHAR(255),
    updated_at        TIMESTAMPTZ,
    updated_by        VARCHAR(255),
    deleted_at        TIMESTAMPTZ,
    version           BIGINT NOT NULL DEFAULT 0
);

-- Reviews
CREATE TABLE reviews (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    booking_id       UUID NOT NULL UNIQUE REFERENCES bookings(id),
    reviewer_id      UUID NOT NULL REFERENCES users(id),
    sub_profile_id   UUID NOT NULL REFERENCES subcontractor_profiles(id),
    overall_rating   NUMERIC(2,1) NOT NULL CHECK (overall_rating BETWEEN 1 AND 5),
    workmanship      NUMERIC(2,1) CHECK (workmanship BETWEEN 1 AND 5),
    punctuality      NUMERIC(2,1) CHECK (punctuality BETWEEN 1 AND 5),
    communication    NUMERIC(2,1) CHECK (communication BETWEEN 1 AND 5),
    brand_knowledge  NUMERIC(2,1) CHECK (brand_knowledge BETWEEN 1 AND 5),
    comment          TEXT,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ,
    deleted_at       TIMESTAMPTZ,
    version          BIGINT NOT NULL DEFAULT 0
);

-- Indexes
CREATE INDEX idx_subcontractor_profiles_city_state ON subcontractor_profiles(city, state);
CREATE INDEX idx_subcontractor_profiles_subscription ON subcontractor_profiles(subscription_tier);
CREATE INDEX idx_trade_specializations_category ON trade_specializations(trade_category);
CREATE INDEX idx_brand_certifications_brand ON brand_certifications(brand_name);
CREATE INDEX idx_bookings_status ON bookings(status);
CREATE INDEX idx_bookings_appointment_at ON bookings(appointment_at);
CREATE INDEX idx_reviews_sub_profile ON reviews(sub_profile_id);
