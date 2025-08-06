package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.utils.constants.EventType;
import ru.yandex.practicum.filmorate.utils.constants.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Класс маппера для преобразования строк базы данных в объекты типа {@link Event}.
 */

public class EventMapper implements RowMapper<Event> {

    /**
     * Преобразует строку результата SQL-запроса в объект типа {@link Event}.
     *
     * @param rs     результирующее множество записей
     * @param rowNum номер строки
     * @return объект типа {@link Event}, соответствующий строке результата
     * @throws SQLException если возникает ошибка при извлечении данных из ResultSet
     */
    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        Event event = new Event();
        event.setEventId(rs.getLong("event_id"));
        event.setTimestamp(rs.getLong("timestamp"));
        event.setUserId(rs.getLong("user_id"));
        event.setEventType(EventType.valueOf(rs.getString("event_type")));
        event.setOperation(Operation.valueOf(rs.getString("operation")));
        event.setEntityId(rs.getLong("entity_id"));
        return event;
    }
}