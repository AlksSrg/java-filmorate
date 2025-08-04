package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Класс маппера для преобразования строк базы данных в объекты типа {@link Director}.
 */
public class DirectorMapper implements RowMapper<Director> {

    /**
     * Преобразует строку результата SQL-запроса в объект типа {@link Director}.
     *
     * @param rs     результирующее множество записей
     * @param rowNum номер строки
     * @return объект типа {@link Director}, соответствующий строке результата
     * @throws SQLException если возникает ошибка при извлечении данных из ResultSet
     */
    @Override
    public Director mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Director.builder()
                .id(rs.getLong("director_id"))
                .name(rs.getString("name"))
                .build();
    }
}