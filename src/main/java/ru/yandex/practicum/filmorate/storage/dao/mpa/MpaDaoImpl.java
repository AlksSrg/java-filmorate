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

@AllArgsConstructor
@Component
public class MpaDaoImpl implements MpaDao {

    private final JdbcTemplate jdbcTemplate;

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
        String sql = """
                SELECT f.film_id, m.mpa_id, m.mpa_name
                FROM film f
                JOIN mpa m ON f.mpa_id = m.mpa_id
                WHERE f.film_id IN (%s)""".formatted(filmIds.stream().map(id -> "?")
                .collect(Collectors.joining(", ")));
        return jdbcTemplate.query(sql, filmIds.toArray(), (rs) -> {
            Map<Long, Mpa> mpaMap = new HashMap<>();
            while (rs.next()) {
                Mpa mpa = new Mpa();
                mpa.setId(rs.getInt("mpa_id"));
                mpa.setName(rs.getString("mpa_name"));

                Long filmId = rs.getLong("film_id");
                mpaMap.put(filmId, mpa);
            }
            return mpaMap;
        });
    }
}