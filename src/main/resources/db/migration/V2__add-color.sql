ALTER TABLE car
    ADD COLUMN (color varchar(200));

UPDATE car
SET color = 'Red'
WHERE color IS NULL;
