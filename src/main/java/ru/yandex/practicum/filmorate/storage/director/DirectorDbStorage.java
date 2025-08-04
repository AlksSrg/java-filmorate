package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.mapper.DirectorMapper;

import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Primary
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Director> getDirectors() {
        return jdbcTemplate.query("SELECT * FROM director", new DirectorMapper());
    }

    @Override
    public Director getDirectorById(Long id) {
        return jdbcTemplate.query("SELECT * FROM director WHERE director_id = ?",
                new DirectorMapper(), id).stream().findFirst().orElse(null);
    }

    @Override
    public Director createDirector(Director director) {
        if (director.getId() != null && getDirectorById(director.getId()) != null) {
            throw new ValidationException("Такой режиссёр уже существует в базе данных.");
        }
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        String insertQuery = "INSERT INTO director (name) VALUES (?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() == null) {
            throw new IllegalStateException("Не сгенерировался ключ пользователя");
        }
        long id = keyHolder.getKey().longValue();
        director.setId(id);
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        if (director.getId() == null) {
            throw new IllegalArgumentException("Id режиссёра должен быть указан.");
        }
        int row = jdbcTemplate.update("UPDATE director SET name = ? WHERE director_id = ?",
                director.getName(), director.getId());
        if (row == 0) {
            throw new EntityNotFoundException(String.format("Режиссёра с id - %s и не существует", director.getId()));
        }
        return director;
    }

    @Override
    public void deleteById(long id) {
        if (jdbcTemplate.update("DELETE FROM director WHERE director_id = ?", id) == 0) {
            throw new EntityNotFoundException(String.format("Режиссёра с id - %s и так не существует", id));
        }
    }

    @Override
    public Set<Director> getDirectorsByFilmId(Long filmId) {
        List<Director> directorsList = jdbcTemplate.query(
                "SELECT d.* FROM director d JOIN film_director fd ON d.director_id = fd.director_id WHERE fd.film_id = ?",
                new DirectorMapper(),
                filmId
        );

        TreeSet<Director> directorsByFilm = new TreeSet<>(Comparator.comparing(Director::getId));
        directorsByFilm.addAll(directorsList);

        return directorsByFilm;
    }

    @Override
    public void addDirectors(Long filmId, Set<Director> directors) {
        if (directors != null) {
            for (Director director : directors) {
                jdbcTemplate.update("INSERT INTO film_director (film_id, director_id) VALUES (?, ?)", filmId, director.getId());
            }
        }
    }

    @Override
    public void deleteDirectorsByFilmId(Long filmId) {
        jdbcTemplate.update("DELETE FROM film_director WHERE film_id = ?", filmId);
    }

    @Override
    public void updateDirectorsForFilm(Long filmId, Set<Director> directors) {
        deleteDirectorsByFilmId(filmId);
        addDirectors(filmId, directors);
    }

    //Этот метод может в будущем пригодиться для наполнения всех фильмов данными о жанрах
    @Override
    public Map<Long, Set<Director>> getAllDirectorsMap() {
        Map<Long, Set<Director>> directorMap = new HashMap<>();
        String sql = """
                SELECT fd.film_id, d.director_id, d.name
                FROM film_director fd
                JOIN director d ON fd.director_id = d.director_id
                ORDER BY fd.film_id""";
        jdbcTemplate.query(sql, (rs, rowNum) -> {
            Director director = Director.builder()
                    .id(rs.getLong("director_id"))
                    .name(rs.getString("name"))
                    .build();
            Long filmId = rs.getLong("film_id");
            if (directorMap.containsKey(filmId)) {
                directorMap.get(filmId).add(director);
            } else {
                Set<Director> directors = new HashSet<>();
                directors.add(director);
                directorMap.put(filmId, directors);
            }
            return director;
        });

        return directorMap;
    }

    @Override
    public Map<Long, Set<Director>> getDirectorMapByFilms(Set<Long> filmIds) {
        String sql = """
                SELECT fd.film_id, d.director_id, d.name
                FROM film_director fd
                JOIN director d ON fd.director_id = d.director_id
                WHERE fd.film_id IN (%s)""".formatted(filmIds.stream().map(id -> "?")
                .collect(Collectors.joining(", ")));
        return jdbcTemplate.query(sql, filmIds.toArray(), (rs) -> {
            Map<Long, Set<Director>> directorMap = new HashMap<>();
            while (rs.next()) {
                Director director = Director.builder()
                        .id(rs.getLong("director_id"))
                        .name(rs.getString("name"))
                        .build();

                Long filmId = rs.getLong("film_id");
                directorMap.computeIfAbsent(filmId, k -> new HashSet<>()).add(director);
            }
            return directorMap;
        });
    }
}