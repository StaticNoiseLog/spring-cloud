CREATE TABLE cat
(
    id   BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100)
);

CREATE TABLE car
(
    id    BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    make  VARCHAR(100),
    model VARCHAR(100),
    year  INT
);

INSERT INTO car (make, model, year)
VALUES ('Honda', 'Civic', 1997);
INSERT INTO car (make, model, year)
VALUES ('Honda', 'Accord', 2003);
INSERT INTO car (make, model, year)
VALUES ('Ford', 'Escort', 1985);
