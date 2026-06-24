CREATE DATABASE bio_os CHARACTER SET UTF8MB4 COLLATE UTF8MB4_UNICODE_CI;

TRUNCATE TABLE gene_rule;

USE bio_os;

SHOW TABLES;

SELECT * FROM user_accounts;

USE bio_os;

SELECT id, user_account_id, water, light, temperature, humidity, total_energy
FROM simulation_log
ORDER BY id DESC;

SELECT * FROM password_hash;

SELECT id, username, password_hash, role, created_at
FROM user_accounts
ORDER BY id DESC;

DELETE FROM growth_timeline;
DELETE FROM growth_simulation;
DELETE FROM simulation_log;
DELETE FROM user_accounts;