package ru.yandex.practicum.filmorate.storage.films;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.storage.mapper.GenreMapper;

@Slf4j
@Component("FilmDbStorage")
@RequiredArgsConstructor
@Primary
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film addFilm(Film film) {
        String sqlInsert =
            "INSERT INTO film (name, description, release_date, duration, mpa_id) VALUES (?,?,?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlInsert,
                Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setLong(4, film.getDuration());
            ps.setLong(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        long generatedId = keyHolder.getKey().longValue();
        film.setId(generatedId);

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        Long filmId = film.getId();
        if (filmId == null) {
            throw new IllegalArgumentException("ID фильма должен быть указан для обновления");
        }

        jdbcTemplate.update(
            "UPDATE film SET name=?, description=?, release_date=?, duration=?, mpa_id=? WHERE film_id=?",
            film.getName(), film.getDescription(), Date.valueOf(film.getReleaseDate()),
            film.getDuration(), film.getMpa().getId(), filmId
        );

        // Если UPDATE прошел успешно, вернём фильм
        return film;
    }

    @Override
    public Collection<Film> getFilms() {
        return jdbcTemplate.query("SELECT * FROM film", new FilmMapper());
    }

    @Override
    public Film getFilmById(Long id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM film WHERE film_id=?",
                new FilmMapper(), id);
        } catch (
            EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("Фильм с id " + id + " не найден");
        }
    }

    @Override
    public Set<Genre> getGenresByFilm(Long filmId) {
        List<Genre> genresList = jdbcTemplate.query(
            "SELECT f.genre_id, g.genre_name FROM film_genre AS f " +
            "LEFT OUTER JOIN genre AS g ON f.genre_id = g.genre_id " +
            "WHERE f.film_id=? ORDER BY g.genre_id",
            new GenreMapper(),
            filmId
        );

        TreeSet<Genre> genresByFilm = new TreeSet<>(Comparator.comparing(Genre::getId));
        genresByFilm.addAll(genresList);

        return genresByFilm;
    }

    @Override
    public Collection<Film> getFilteredFilms(Integer genreId, Integer year) {
        StringBuilder sql = new StringBuilder("SELECT f.* FROM film f");
        List<Object> params = new ArrayList<>();

        if (genreId != null) {
            sql.append(" JOIN film_genre fg ON f.film_id = fg.film_id WHERE fg.genre_id = ?");
            params.add(genreId);
        }

        if (year != null) {
            sql.append(genreId != null ? " AND" : " WHERE")
                .append(" EXTRACT(YEAR FROM f.release_date) = ?");
            params.add(year);
        }

        try {
            return jdbcTemplate.query(sql.toString(), new FilmMapper(), params.toArray());
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public void deleteById(long id) {
        if (jdbcTemplate.update("DELETE FROM film WHERE film_id = ?", id) == 0) {
            throw new EntityNotFoundException(
                String.format("Фильма с id %s и так не существует", id));
        }
    }

    /**
     * Поиск фильмов в базе данных по заданным критериям
     *
     * @param query текст для поиска (подстрока)
     * @param by    критерии поиска: "title", "director" или "title,director"
     * @return список найденных фильмов, отсортированных по рейтингу (по убыванию)
     */
    @Override
    public List<Film> searchFilms(String query, String by) {
        String sql = "SELECT DISTINCT f.* FROM films f ";

        if (by.contains("director")) {
            sql += "LEFT JOIN film_director fd ON f.film_id = fd.film_id " +
                   "LEFT JOIN directors d ON fd.director_id = d.director_id ";
        }

        sql += "WHERE ";

        List<String> conditions = new ArrayList<>();
        if (by.contains("title")) {
            conditions.add("LOWER(f.name) LIKE LOWER(CONCAT('%', ?, '%'))");
        }
        if (by.contains("director")) {
            conditions.add("LOWER(d.name) LIKE LOWER(CONCAT('%', ?, '%'))");
        }

        sql += String.join(" OR ", conditions) + " " +
               "ORDER BY f.rate DESC";

        return jdbcTemplate.query(sql, ps -> {
            int paramIndex = 1;
            if (by.contains("title")) {
                ps.setString(paramIndex++, query);
            }
            if (by.contains("director")) {
                ps.setString(paramIndex, query);
            }
        }, new FilmMapper());
    }
}