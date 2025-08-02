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

INSERT INTO users (user_id, email, login, name, birthday) VALUES
(1, 'user1@example.com', 'login1', 'User One', '1990-01-01'),
(2, 'user2@example.com', 'login2', 'User Two', '1990-02-02'),
(3, 'user3@example.com', 'login3', 'User Three', '1990-03-03');

-- Тестовые фильмы
INSERT INTO film (film_id, name, description, release_date, duration, mpa_id) VALUES
(1, 'Film One', 'First test film', '2020-01-01', 120, 1),
(2, 'Film Two', 'Second test film', '2020-02-02', 90, 2),
(3, 'Film Three', 'Third test film', '2020-03-03', 150, 3);

-- Тестовые отзывы
INSERT INTO reviews (review_id, content, is_positive, user_id, film_id, useful) VALUES
(1, 'Not very good', false, 1, 1, 0),
(2, 'Pretty good', true, 2, 1, 0),
(3, 'Excellent film', true, 3, 2, 0);

-- Тестовые лайки/дизлайки отзывов
INSERT INTO review_likes (review_id, user_id, is_like) VALUES
(1, 2, false), -- user2 дизлайкнул отзыв1
(2, 1, true),  -- user1 лайкнул отзыв2
(3, 1, true);  -- user1 лайкнул отзыв3

-- Обновление рейтинга полезности отзывов
UPDATE reviews SET useful = -1 WHERE review_id = 1;
UPDATE reviews SET useful = 1 WHERE review_id = 2;
UPDATE reviews SET useful = 1 WHERE review_id = 3;