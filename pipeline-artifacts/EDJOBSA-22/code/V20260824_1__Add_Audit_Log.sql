-- Flyway Migration: V20260824_1__Add_Audit_Log.sql
-- Task A-1: Create audit log table for self-service borrowing feature (EDJOBSA-22)

-- Create the audit_log table
CREATE TABLE IF NOT EXISTS audit_log (
    id UUID PRIMARY KEY NOT NULL DEFAULT gen_random_uuid(),
    member_id UUID NOT NULL,
    book_id UUID NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    outcome VARCHAR(50) NOT NULL,
    correlation_id VARCHAR(36) NOT NULL,
    message TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for common queries
CREATE INDEX IF NOT EXISTS idx_audit_member_timestamp ON audit_log(member_id, timestamp DESC);
CREATE INDEX IF NOT EXISTS idx_audit_book_timestamp ON audit_log(book_id, timestamp DESC);
CREATE INDEX IF NOT EXISTS idx_audit_correlation_id ON audit_log(correlation_id);
CREATE INDEX IF NOT EXISTS idx_audit_outcome ON audit_log(outcome);

-- Add comments for clarity
COMMENT ON TABLE audit_log IS 'Immutable audit trail for all book checkout attempts (success and failure)';
COMMENT ON COLUMN audit_log.member_id IS 'UUID of the member attempting checkout';
COMMENT ON COLUMN audit_log.book_id IS 'UUID of the book being requested (00000000-0000-0000-0000-000000000000 if not found)';
COMMENT ON COLUMN audit_log.timestamp IS 'When the checkout attempt occurred';
COMMENT ON COLUMN audit_log.outcome IS 'Result: SUCCESS, FINES_OUTSTANDING, LIMIT_EXCEEDED, BOOK_UNAVAILABLE, BOOK_NOT_FOUND, ERROR';
COMMENT ON COLUMN audit_log.correlation_id IS 'Request correlation ID for distributed tracing';
COMMENT ON COLUMN audit_log.message IS 'Optional error or context message (max 1000 chars)';

