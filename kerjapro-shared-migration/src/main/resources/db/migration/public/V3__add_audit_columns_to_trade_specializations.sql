-- ============================================================
-- V3: Add missing audit columns to trade_specializations
-- BaseEntity requires created_by and updated_by on all tables
-- ============================================================

ALTER TABLE trade_specializations
    ADD COLUMN IF NOT EXISTS created_by VARCHAR(255),
    ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);
