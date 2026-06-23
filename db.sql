CREATE DATABASE bio_os CHARACTER SET UTF8MB4 COLLATE UTF8MB4_UNICODE_CI;

TRUNCATE TABLE gene_rule;

USE bio_os;

SHOW TABLES;

SELECT * FROM user_accounts;

USE bio_os;

SELECT id, user_account_id, water, light, temperature, humidity, total_energy
FROM simulation_log
ORDER BY id DESC;