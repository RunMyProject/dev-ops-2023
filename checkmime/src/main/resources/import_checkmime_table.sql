------------------------------
-- Build Checkmime
-- by Edoardo Sabatini
-- @2023
------------------------------
INSERT INTO checkmimes (id, format, description, enabled)
SELECT * FROM (SELECT 1, 'PDF', "PDF file", true) AS tmp
WHERE NOT EXISTS (
    SELECT format FROM checkmimes WHERE format = 'PDF'
) LIMIT 1;
INSERT INTO checkmimes (id, format, description, enabled)
SELECT * FROM (SELECT 2, 'TXT', "Text file", false) AS tmp
WHERE NOT EXISTS (
    SELECT format FROM checkmimes WHERE format = 'TXT'
) LIMIT 1;
INSERT INTO checkmimes (id, format, description, enabled)
SELECT * FROM (SELECT 3, 'java', "Java file", false) AS tmp
WHERE NOT EXISTS (
    SELECT format FROM checkmimes WHERE format = 'java'
) LIMIT 1;
INSERT INTO checkmimes (id, format, description, enabled)
SELECT * FROM (SELECT 4, 'p7m', "digitally signed file", true) AS tmp
WHERE NOT EXISTS (
    SELECT format FROM checkmimes WHERE format = 'p7m'
) LIMIT 1;
