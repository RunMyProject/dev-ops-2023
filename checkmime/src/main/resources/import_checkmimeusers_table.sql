------------------------------
-- Build Checkmimeusers
-- by Edoardo Sabatini
-- @2023
------------------------------
INSERT INTO checkmimeusers (id, username, password, logged)
SELECT * FROM (SELECT 1, 'edoardo', 'password', false) AS tmp
WHERE NOT EXISTS (
    SELECT username FROM checkmimeusers WHERE username = 'edoardo'
) LIMIT 1;
INSERT INTO checkmimeusers (id, username, password, logged)
SELECT * FROM (SELECT 2, 'admin', '1234567', false) AS tmp
WHERE NOT EXISTS (
    SELECT username FROM checkmimeusers WHERE username = 'admin'
) LIMIT 1;
