package ru.yandex.practicum.filmorate.storage.mapper;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.constants.EventType;
import ru.yandex.practicum.filmorate.model.constants.Operation;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Класс маппера для преобразования строк базы данных в объекты типа {@link Event}.
 */
public class EventMapper implements RowMapper<Event> {

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
