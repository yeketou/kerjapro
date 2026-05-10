-- ============================================================
-- V2: Change subcontractor_profiles.user_id from UUID FK
--     to VARCHAR storing the Keycloak subject directly.
--
-- Rationale: Keycloak is the source of truth for user identity.
-- Storing the sub (keycloak_id) directly avoids an extra users
-- table lookup on every profile request and simplifies local dev.
-- ============================================================

-- Drop FK constraint first
ALTER TABLE subcontractor_profiles
    DROP CONSTRAINT IF EXISTS subcontractor_profiles_user_id_fkey;

-- Change column type to VARCHAR
ALTER TABLE subcontractor_profiles
    ALTER COLUMN user_id TYPE VARCHAR(255) USING user_id::text;

-- Same fix for global_reviews reviewer_user_id (UUID → VARCHAR for Keycloak sub)
ALTER TABLE global_reviews
    DROP CONSTRAINT IF EXISTS global_reviews_reviewer_user_id_fkey;

ALTER TABLE global_reviews
    ALTER COLUMN reviewer_user_id TYPE VARCHAR(255) USING reviewer_user_id::text;
