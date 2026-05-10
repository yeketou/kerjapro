-- ============================================================
-- KerjaPro Tenant Schema Core Setup
-- V1: Per-tenant tables (projects, packages, bookings, reviews)
-- Applied once per tenant on provisioning via TenantSchemaService
-- ============================================================

-- ============================================================
-- PROJECTS
-- ============================================================
CREATE TABLE projects (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    main_contractor_id   UUID NOT NULL,   -- references public.users(id)
    title                VARCHAR(255) NOT NULL,
    description          TEXT,
    location             VARCHAR(255),
    start_date           DATE,
    end_date             DATE,
    status               VARCHAR(30) NOT NULL DEFAULT 'DRAFT'
                             CHECK (status IN ('DRAFT', 'ACTIVE', 'COMPLETED', 'CANCELLED')),
    created_at           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by           VARCHAR(255),
    updated_at           TIMESTAMPTZ,
    updated_by           VARCHAR(255),
    deleted_at           TIMESTAMPTZ,
    version              BIGINT NOT NULL DEFAULT 0
);

-- ============================================================
-- WORK PACKAGES
-- ============================================================
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
    status           VARCHAR(30) NOT NULL DEFAULT 'OPEN'
                         CHECK (status IN ('OPEN', 'ASSIGNED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
    assigned_sub_id  UUID,    -- references public.subcontractor_profiles(id)
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by       VARCHAR(255),
    updated_at       TIMESTAMPTZ,
    updated_by       VARCHAR(255),
    deleted_at       TIMESTAMPTZ,
    version          BIGINT NOT NULL DEFAULT 0
);

-- ============================================================
-- BOOKINGS / APPOINTMENTS
-- ============================================================
CREATE TABLE bookings (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    work_package_id      UUID REFERENCES work_packages(id),
    main_contractor_id   UUID NOT NULL,   -- references public.users(id)
    sub_profile_id       UUID NOT NULL,   -- references public.subcontractor_profiles(id)
    appointment_at       TIMESTAMPTZ NOT NULL,
    duration_minutes     INT NOT NULL DEFAULT 60,
    location             VARCHAR(255),
    notes                TEXT,
    status               VARCHAR(30) NOT NULL DEFAULT 'PENDING'
                             CHECK (status IN ('PENDING', 'CONFIRMED', 'DECLINED', 'COMPLETED', 'CANCELLED')),
    cancelled_reason     TEXT,
    created_at           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by           VARCHAR(255),
    updated_at           TIMESTAMPTZ,
    updated_by           VARCHAR(255),
    deleted_at           TIMESTAMPTZ,
    version              BIGINT NOT NULL DEFAULT 0
);

-- ============================================================
-- TENANT REVIEWS (internal — only visible within this tenant)
-- After COMPLETED booking, review is created here AND
-- an anonymised aggregate is pushed to public.global_reviews
-- ============================================================
CREATE TABLE tenant_reviews (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    booking_id       UUID NOT NULL UNIQUE REFERENCES bookings(id),
    reviewer_id      UUID NOT NULL,   -- references public.users(id)
    sub_profile_id   UUID NOT NULL,   -- references public.subcontractor_profiles(id)
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
-- TENANT SUBCONTRACTOR NETWORK
-- Private curated pool — Enterprise tenants can invite/save
-- specific subs to their preferred network
-- ============================================================
CREATE TABLE tenant_sub_network (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sub_profile_id  UUID NOT NULL,   -- references public.subcontractor_profiles(id)
    added_by        UUID NOT NULL,   -- references public.users(id)
    notes           TEXT,
    is_preferred    BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    UNIQUE (sub_profile_id)
);

-- ============================================================
-- TENANT SETTINGS
-- ============================================================
CREATE TABLE tenant_settings (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    setting_key     VARCHAR(100) NOT NULL UNIQUE,
    setting_value   TEXT,
    description     VARCHAR(500),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

INSERT INTO tenant_settings (setting_key, setting_value, description) VALUES
    ('timezone',         'Asia/Kuala_Lumpur', 'Display timezone for this tenant'),
    ('currency',         'MYR',               'Currency for budget display'),
    ('booking_lead_days','2',                 'Minimum days ahead for booking appointments'),
    ('auto_review_push', 'true',              'Auto-push reviews to public marketplace');

-- ============================================================
-- INDEXES
-- ============================================================
CREATE INDEX idx_projects_contractor ON projects(main_contractor_id);
CREATE INDEX idx_projects_status ON projects(status);
CREATE INDEX idx_work_packages_project ON work_packages(project_id);
CREATE INDEX idx_work_packages_trade ON work_packages(trade_category);
CREATE INDEX idx_work_packages_status ON work_packages(status);
CREATE INDEX idx_work_packages_sub ON work_packages(assigned_sub_id);
CREATE INDEX idx_bookings_status ON bookings(status);
CREATE INDEX idx_bookings_appointment ON bookings(appointment_at);
CREATE INDEX idx_bookings_sub ON bookings(sub_profile_id);
CREATE INDEX idx_tenant_reviews_sub ON tenant_reviews(sub_profile_id);
CREATE INDEX idx_tenant_sub_network_sub ON tenant_sub_network(sub_profile_id);
