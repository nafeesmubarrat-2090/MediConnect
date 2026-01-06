-- SQL script to add consultation_duration column to existing doctors table
-- Run this if you have an existing database with doctors

USE mediconnect;

-- Check if column exists and add it if it doesn't
ALTER TABLE doctors ADD COLUMN consultation_duration INT DEFAULT 30;

-- Update existing records to have default value
UPDATE doctors SET consultation_duration = 30 WHERE consultation_duration IS NULL;

-- Verify the change
SELECT username, name, consultation_duration FROM doctors;

