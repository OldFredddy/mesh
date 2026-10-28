----------------------------------------------------------------------
-- user #1: Иван
----------------------------------------------------------------------
INSERT INTO users (id, name, date_of_birth, password)
VALUES (1, 'Ivan Petrov', '1990-05-01',
        '$2y$10$fT3fed/n0855dtn1pO9bY.JhMr3KKN0rLMYFt0oM3jDBRx9IS7EIC');

INSERT INTO accounts   VALUES (1, 1, 100.00, 100.00);
INSERT INTO email_data VALUES (1, 1, 'ivan-megaman@gmail.com');
INSERT INTO phone_data VALUES (1, 1, '78005553535');

----------------------------------------------------------------------
-- user #2: Ольга
----------------------------------------------------------------------
INSERT INTO users VALUES
    (2, 'Olga Ivanova', '1998-11-20',
     '$2y$10$fT3fed/n0855dtn1pO9bY.JhMr3KKN0rLMYFt0oM3jDBRx9IS7EIC');
INSERT INTO accounts   VALUES (2, 2, 250.00, 250.00);
INSERT INTO email_data VALUES (2, 2, 'olga_12341234@protonmail.com');
INSERT INTO phone_data VALUES (2, 2, '711111111111');

----------------------------------------------------------------------
-- user #3: Сергей
----------------------------------------------------------------------
INSERT INTO users VALUES
    (3, 'Sergey Smirnov', '1995-02-14',
     '$2y$10$fT3fed/n0855dtn1pO9bY.JhMr3KKN0rLMYFt0oM3jDBRx9IS7EIC');
INSERT INTO accounts   VALUES (3, 3, 150.00, 150.00);
INSERT INTO email_data VALUES (3, 3, 'sergey@example.com');
INSERT INTO phone_data VALUES (3, 3, '79001234567');

----------------------------------------------------------------------
-- user #4: Анна
----------------------------------------------------------------------
INSERT INTO users VALUES
    (4, 'Anna Petrova', '1992-07-30',
     '$2y$10$fT3fed/n0855dtn1pO9bY.JhMr3KKN0rLMYFt0oM3jDBRx9IS7EIC');
INSERT INTO accounts   VALUES (4, 4, 320.00, 320.00);
INSERT INTO email_data VALUES (4, 4, 'anna@example.com');
INSERT INTO phone_data VALUES (4, 4, '79201234567');

----------------------------------------------------------------------
-- user #5: Дмитрий
----------------------------------------------------------------------
INSERT INTO users VALUES
    (5, 'Dmitry Kuznetsov', '1985-12-05',
     '$2y$10$fT3fed/n0855dtn1pO9bY.JhMr3KKN0rLMYFt0oM3jDBRx9IS7EIC');
INSERT INTO accounts   VALUES (5, 5, 500.00, 500.00);
INSERT INTO email_data VALUES (5, 5, 'dmitry@example.com');
INSERT INTO phone_data VALUES (5, 5, '79301234567');

----------------------------------------------------------------------
-- user #6: Мария
----------------------------------------------------------------------
INSERT INTO users VALUES
    (6, 'Maria Sokolova', '1999-03-22',
     '$2y$10$fT3fed/n0855dtn1pO9bY.JhMr3KKN0rLMYFt0oM3jDBRx9IS7EIC');
INSERT INTO accounts   VALUES (6, 6,  80.00,  80.00);
INSERT INTO email_data VALUES (6, 6, 'maria@example.com');
INSERT INTO phone_data VALUES (6, 6, '79601234567');

----------------------------------------------------------------------
-- user #7: Алексей
----------------------------------------------------------------------
INSERT INTO users VALUES
    (7, 'Alexey Popov', '1980-09-11',
     '$2y$10$fT3fed/n0855dtn1pO9bY.JhMr3KKN0rLMYFt0oM3jDBRx9IS7EIC');
INSERT INTO accounts   VALUES (7, 7, 1000.00, 1000.00);
INSERT INTO email_data VALUES (7, 7, 'alexey@example.com');
INSERT INTO phone_data VALUES (7, 7, '79771234567');


-- Update sequences after inserting explicit ids.
SELECT setval(pg_get_serial_sequence('mesh.users', 'id'), (SELECT MAX(id) FROM mesh.users));
SELECT setval(pg_get_serial_sequence('mesh.accounts', 'id'), (SELECT MAX(id) FROM mesh.accounts));
SELECT setval(pg_get_serial_sequence('mesh.email_data', 'id'), (SELECT MAX(id) FROM mesh.email_data));
SELECT setval(pg_get_serial_sequence('mesh.phone_data', 'id'), (SELECT MAX(id) FROM mesh.phone_data));
