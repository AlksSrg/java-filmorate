INSERT INTO mpa (mpa_id, mpa_name)
            VALUES (1, 'G');
INSERT INTO mpa (mpa_id, mpa_name)
            VALUES (2, 'PG');
INSERT INTO mpa (mpa_id, mpa_name)
            VALUES (3, 'PG-13');
INSERT INTO mpa (mpa_id, mpa_name)
            VALUES (4, 'R');
INSERT INTO mpa (mpa_id, mpa_name)
            VALUES (5, 'NC-17');

INSERT INTO genre (genre_name) VALUES ('Комедия');
INSERT INTO genre (genre_name) VALUES ('Драма');
INSERT INTO genre (genre_name) VALUES ('Мультфильм');
INSERT INTO genre (genre_name) VALUES ('Триллер');
INSERT INTO genre (genre_name) VALUES ('Документальный');
INSERT INTO genre (genre_name) VALUES ('Боевик');


-- Сбрасываем sequence для автоинкремента
ALTER TABLE reviews ALTER COLUMN review_id RESTART WITH 1;

-- Тестовые пользователи (должны соответствовать тестам)
INSERT INTO users (user_id, email, login, name, birthday) VALUES
(1, 'user1@test.com', 'login1', 'User 1', '1990-01-01'),
(2, 'user2@test.com', 'login2', 'User 2', '1990-02-02'),
(3, 'user3@test.com', 'login3', 'User 3', '1990-03-03');

-- Тестовые фильмы (должны соответствовать тестам)
INSERT INTO film (film_id, name, description, release_date, duration, mpa_id) VALUES
(1, 'Film 1', 'Description 1', '2020-01-01', 120, 1),
(2, 'Film 2', 'Description 2', '2020-02-02', 90, 2);