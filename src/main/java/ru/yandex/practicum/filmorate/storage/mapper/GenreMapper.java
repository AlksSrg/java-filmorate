package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Класс маппера для преобразования строк базы данных в объекты типа {@link Genre}.
 */

public class GenreMapper implements RowMapper<Genre> {

    /**
     * Преобразует строку результата SQL-запроса в объект типа {@link Genre}.
     *
     * @param rs     результирующее множество записей
     * @param rowNum номер строки
     * @return объект типа {@link Genre}, соответствующий строке результата
     * @throws SQLException если возникает ошибка при извлечении данных из ResultSet
     */
    @Override
    public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setId(rs.getInt("genre_id"));
        genre.setName(rs.getString("genre_name"));
        return genre;
    }
}