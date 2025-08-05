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

@Slf4j
@Component
@RequiredArgsConstructor
public class EventDaoImpl implements EventDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addEvent(Long userId, EventType eventType, Operation operation, Long entityId) {
        String sql = "INSERT INTO events (timestamp, user_id, event_type, operation, entity_id) VALUES (?, ?, ?, ?, ?)";
        long timestamp = System.currentTimeMillis();
        jdbcTemplate.update(sql, timestamp, userId, eventType.toString(), operation.toString(), entityId);
        log.debug("Добавлено событие: userId={}, eventType={}, operation={}, entityId={}",
                userId, eventType, operation, entityId);
    }

    @Override
    public List<Event> getUserFeed(Long userId) {
        String sql = "SELECT e.* FROM events e " +
                "WHERE e.user_id = ? " +
                "OR e.user_id IN (SELECT friend_id FROM friends WHERE user_id = ?) " +
                "ORDER BY e.timestamp ASC";
        List<Event> events = jdbcTemplate.query(sql, new EventMapper(), userId, userId);
        log.info("Получена лента для пользователя {}: {} событий", userId, events.size());

        // Для отладки выведем все события
        if (events.isEmpty()) {
            log.warn("Лента пустая для пользователя {}. Проверяем все события в БД:", userId);
            List<Event> allEvents = getAllEvents();
            log.info("Всего событий в БД: {}", allEvents.size());
            for (Event e : allEvents) {
                log.info("Событие: userId={}, eventType={}, operation={}, entityId={}",
                        e.getUserId(), e.getEventType(), e.getOperation(), e.getEntityId());
            }
        }

        return events;
    }

    public List<Event> getAllEvents() {
        String sql = "SELECT * FROM events ORDER BY timestamp ASC";
        return jdbcTemplate.query(sql, new EventMapper());
    }
}
