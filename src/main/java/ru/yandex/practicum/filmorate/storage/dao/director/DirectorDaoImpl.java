package ru.yandex.practicum.filmorate.storage.dao.director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;

/**
 * Реализация DAO для работы с режиссерами в базе данных
 */
@Repository
public class DirectorDaoImpl implements DirectorDao {

    private final JdbcTemplate jdbcTemplate;

    public DirectorDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Получение списка режиссеров для указанного фильма
     *
     * @param filmId идентификатор фильма
     * @return список режиссеров
     */
    @Override
    public List<Director> getFilmDirectors(long filmId) {
        String sql = "SELECT d.* FROM directors d " +
                     "JOIN film_director fd ON d.director_id = fd.director_id " +
                     "WHERE fd.film_id = ?";
        return jdbcTemplate.query(sql, this::mapRow, filmId);
    }

    /**
     * Маппинг результата SQL-запроса в объект Director
     *
     * @param rs     ResultSet с данными
     * @param rowNum номер строки
     * @return объект Director
     * @throws SQLException при ошибках работы с ResultSet
     */
    private Director mapRow(ResultSet rs, int rowNum) throws SQLException {
        Director director = new Director();
        director.setId(rs.getLong("director_id"));
        director.setName(rs.getString("name"));
        return director;
    }
}
