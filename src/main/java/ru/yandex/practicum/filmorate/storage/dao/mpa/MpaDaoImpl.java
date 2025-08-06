package ru.yandex.practicum.filmorate.storage.dao.mpa;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mapper.MpaMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Реализация DAO для работы с рейтингами MPA.
 * Обеспечивает доступ к данным о возрастных рейтингах фильмов.
 */

@AllArgsConstructor
@Component
public class MpaDaoImpl implements MpaDao {

    private final JdbcTemplate jdbcTemplate;
    private static final String GET_MPA_BY_FILMS_SQL = """
            SELECT f.film_id, m.mpa_id, m.mpa_name
            FROM film f
            JOIN mpa m ON f.mpa_id = m.mpa_id
            WHERE f.film_id IN (%s)""";

    @Override
    public Mpa getMpaById(Integer id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM mpa WHERE mpa_id=?", new MpaMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("MPA с id " + id + " не найден");
        }
    }

    @Override
    public List<Mpa> getListMpa() {
        return jdbcTemplate.query("SELECT * FROM mpa ORDER BY mpa_id", new MpaMapper());
    }

    @Override
    public Map<Long, Mpa> getMpaMapByFilms(Set<Long> filmIds) {
        String inClause = filmIds.stream()
                .map(id -> "?")
                .collect(Collectors.joining(", "));

        String sql = String.format(GET_MPA_BY_FILMS_SQL, inClause);

        return jdbcTemplate.query(sql, filmIds.toArray(), (rs) -> {
            Map<Long, Mpa> mpaMap = new HashMap<>();
            while (rs.next()) {
                Mpa mpa = Mpa.builder()
                        .id(rs.getInt("mpa_id"))
                        .name(rs.getString("mpa_name"))
                        .build();

                Long filmId = rs.getLong("film_id");
                mpaMap.put(filmId, mpa);
            }
            return mpaMap;
        });
    }
}