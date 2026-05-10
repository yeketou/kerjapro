-- ============================================================
-- KerjaPro Public Schema Foundation
-- V1: Shared marketplace tables (visible to all tenants)
-- ============================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ============================================================
-- TENANTS
-- ============================================================
CREATE TABLE tenants (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    slug          VARCHAR(100) NOT NULL UNIQUE,   -- used as schema name: tenant_{slug}
    company_name  VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    phone         VARCHAR(30),
    logo_url      TEXT,
    plan          VARCHAR(30) NOT NULL DEFAULT 'STARTER'
                      CHECK (plan IN ('STARTER', 'PROFESSIONAL', 'ENTERPRISE')),
    status        VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
                      CHECK (status IN ('ACTIVE', 'SUSPENDED', 'CANCELLED')),
    schema_name   VARCHAR(100) GENERATED ALWAYS AS ('tenant_' || slug) STORED,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ,
    deleted_at    TIMESTAMPTZ,
    version       BIGINT NOT NULL DEFAULT 0
);

-- ============================================================
-- USERS
-- ============================================================
CREATE TABLE users (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    keycloak_id   VARCHAR(255) NOT NULL UNIQUE,
    email         VARCHAR(255) NOT NULL UNIQUE,
    full_name     VARCHAR(255),
    phone         VARCHAR(30),
    role          VARCHAR(50)  NOT NULL
                      CHECK (role IN ('MAIN_CONTRACTOR', 'SUBCONTRACTOR', 'PLATFORM_ADMIN')),
    -- tenant_id is set for MAIN_CONTRACTOR users, NULL for SUBCONTRACTOR (they are platform-wide)
    tenant_id     UUID REFERENCES tenants(id),
    is_active     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ,
    deleted_at    TIMESTAMPTZ,
    version       BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT chk_main_contractor_has_tenant
        CHECK (role != 'MAIN_CONTRACTOR' OR tenant_id IS NOT NULL)
);

-- ============================================================
-- SUBCONTRACTOR PROFILES (Global marketplace — no tenant_id)
-- ============================================================
CREATE TABLE subcontractor_profiles (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id               UUID NOT NULL UNIQUE REFERENCES users(id),
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
    subscription_tier     VARCHAR(20) NOT NULL DEFAULT 'FREE'
                              CHECK (subscription_tier IN ('FREE', 'PRO', 'PREMIUM')),
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

-- ============================================================
-- TRADE SPECIALIZATIONS
-- ============================================================
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

-- ============================================================
-- BRAND CERTIFICATIONS
-- ============================================================
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

-- ============================================================
-- PORTFOLIO
-- ============================================================
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

-- ============================================================
-- GLOBAL REVIEWS (public aggregate — drives marketplace rating)
-- ============================================================
CREATE TABLE global_reviews (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sub_profile_id   UUID NOT NULL REFERENCES subcontractor_profiles(id),
    reviewer_user_id UUID NOT NULL REFERENCES users(id),
    tenant_id        UUID REFERENCES tenants(id),  -- which tenant this review came from
    overall_rating   NUMERIC(2,1) NOT NULL CHECK (overall_rating BETWEEN 1 AND 5),
    workmanship      NUMERIC(2,1) CHECK (workmanship BETWEEN 1 AND 5),
    punctuality      NUMERIC(2,1) CHECK (punctuality BETWEEN 1 AND 5),
    communication    NUMERIC(2,1) CHECK (communication BETWEEN 1 AND 5),
    brand_knowledge  NUMERIC(2,1) CHECK (brand_knowledge BETWEEN 1 AND 5),
    comment          TEXT,
    is_public        BOOLEAN NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ,
    deleted_at       TIMESTAMPTZ,
    version          BIGINT NOT NULL DEFAULT 0
);

-- ============================================================
-- INDEXES
-- ============================================================
CREATE INDEX idx_users_tenant ON users(tenant_id);
CREATE INDEX idx_users_keycloak ON users(keycloak_id);
CREATE INDEX idx_sub_profiles_city_state ON subcontractor_profiles(city, state);
CREATE INDEX idx_sub_profiles_tier ON subcontractor_profiles(subscription_tier);
CREATE INDEX idx_sub_profiles_verified ON subcontractor_profiles(is_verified);
CREATE INDEX idx_sub_profiles_rating ON subcontractor_profiles(average_rating DESC);
CREATE INDEX idx_trade_specs_category ON trade_specializations(trade_category);
CREATE INDEX idx_brand_certs_brand ON brand_certifications(brand_name);
CREATE INDEX idx_brand_certs_verified ON brand_certifications(is_verified);
CREATE INDEX idx_global_reviews_sub ON global_reviews(sub_profile_id);
CREATE INDEX idx_tenants_slug ON tenants(slug);
CREATE INDEX idx_tenants_status ON tenants(status);
