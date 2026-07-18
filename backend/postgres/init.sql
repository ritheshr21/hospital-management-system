-- Postgres runs this once on first startup (empty data volume).
-- The default database (hms_auth_db) is created by POSTGRES_DB; we add the rest here.
CREATE DATABASE hms_patient_db;
CREATE DATABASE hms_doctor_db;
CREATE DATABASE hms_appointment_db;
CREATE DATABASE hms_billing_db;
CREATE DATABASE hms_notification_db;
CREATE DATABASE hms_pharmacy_db;
