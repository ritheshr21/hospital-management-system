-- Run against hms_notification_db when upgrading an EXISTING deployment to Part 6.
--
-- Why this is needed: Hibernate's ddl-auto=update creates a CHECK constraint for
-- @Enumerated(STRING) columns, but it does NOT alter that constraint when a new
-- enum value is added. Adding STOCK_LOW therefore fails on databases created
-- before Part 6 with:
--   ERROR: new row for relation "notifications" violates check constraint
--          "notifications_type_check"
-- Fresh databases are unaffected (the constraint is created with all values).
--
-- This is exactly why a real deployment should use Flyway/Liquibase rather than
-- ddl-auto=update.

ALTER TABLE notifications DROP CONSTRAINT IF EXISTS notifications_type_check;

ALTER TABLE notifications ADD CONSTRAINT notifications_type_check
    CHECK (type IN ('APPOINTMENT_COMPLETED', 'BILL_PAID', 'STOCK_LOW'));
