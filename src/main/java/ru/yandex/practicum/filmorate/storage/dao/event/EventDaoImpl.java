package ru.yandex.practicum.filmorate.storage.dao.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.constants.EventType;
import ru.yandex.practicum.filmorate.model.constants.Operation;
import ru.yandex.practicum.filmorate.storage.mapper.EventMapper;

import java.util.List;

/**
 * Реализация DAO для работы с событиями пользователей.
 * Обеспечивает хранение и получение событий в базе данных.
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class EventDaoImpl implements EventDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addEvent(Long userId, EventType eventType, Operation operation, Long entityId) {
        if (userId == null || eventType == null || operation == null || entityId == null) {
            throw new IllegalArgumentException("Параметры события не могут быть null");
        }
        String sql = "INSERT INTO events (timestamp, user_id, event_type, operation, entity_id) VALUES (?, ?, ?, ?, ?)";
        long timestamp = System.currentTimeMillis();
        jdbcTemplate.update(sql, timestamp, userId, eventType.toString(), operation.toString(), entityId);
        log.debug("Добавлено событие: userId={}, eventType={}, operation={}, entityId={}",
                userId, eventType, operation, entityId);
    }

    @Override
    public List<Event> getUserFeed(Long userId) {
        String sql = "SELECT * FROM events WHERE user_id = ? ORDER BY event_id ASC";
        return jdbcTemplate.query(sql, new EventMapper(), userId);
    }

    public List<Event> getAllEvents() {
        String sql = "SELECT * FROM events ORDER BY timestamp ASC";
        return jdbcTemplate.query(sql, new EventMapper());
    }
}