package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Класс маппера для преобразования строк базы данных в объекты типа {@link Mpa}.
 */
public class MpaMapper implements RowMapper<Mpa> {

    /**
     * Преобразует строку результата SQL-запроса в объект типа {@link Mpa}.
     *
     * @param rs     результирующее множество записей
     * @param rowNum номер строки
     * @return объект типа {@link Mpa}, соответствующий строке результата
     * @throws SQLException если возникает ошибка при извлечении данных из ResultSet
     */
    @Override
    public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("mpa_id"));
        mpa.setName(rs.getString("mpa_name"));
        return mpa;
    }
}