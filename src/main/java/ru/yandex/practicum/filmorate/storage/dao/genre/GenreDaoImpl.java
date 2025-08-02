package ru.yandex.practicum.filmorate.storage.dao.genre;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mapper.GenreMapper;

import java.util.*;

@AllArgsConstructor
@Component
public class GenreDaoImpl implements GenreDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre getGenreById(Integer id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM genre WHERE genre_id = ?", new GenreMapper(), id);
        } catch (EmptyResultDataAccessException exception) {
            throw new EntityNotFoundException(String.format("Жанра с id %s не существует", id));
        }
    }

    @Override
    public Set<Genre> getGenres() {
        return new LinkedHashSet<>(
                jdbcTemplate.query("SELECT * FROM genre ORDER BY genre_id", new GenreMapper())
        );
    }

    @Override
    public void addGenres(Long filmId, Set<Genre> genres) {
        if (genres != null) {
            for (Genre genre : genres) {
                jdbcTemplate.update("INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)", filmId, genre.getId());
            }
        }
    }

    public void deleteGenres(Long filmId) {
        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id=?", filmId);
    }

    @Override
    public void updateGenres(Long filmId, Set<Genre> genres) {
        deleteGenres(filmId);
        if (genres != null) {
            addGenres(filmId, genres);
        }
    }

    @Override
    public Set<Genre> getGenresByFilm(Long filmId) {
        List<Genre> genresList = jdbcTemplate.query(
                "SELECT g.* FROM genre g JOIN film_genre fg ON g.genre_id = fg.genre_id WHERE fg.film_id = ?",
                new GenreMapper(),
                filmId
        );

        TreeSet<Genre> genresByFilm = new TreeSet<>(Comparator.comparing(Genre::getId));
        genresByFilm.addAll(genresList);

        return genresByFilm;
    }
}