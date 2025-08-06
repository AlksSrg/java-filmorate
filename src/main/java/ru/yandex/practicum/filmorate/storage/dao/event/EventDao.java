package ru.yandex.practicum.filmorate.storage.dao.event;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.constants.EventType;
import ru.yandex.practicum.filmorate.model.constants.Operation;

import java.util.List;

/**
 * Интерфейс для работы с событиями пользователей.
 */

public interface EventDao {

    /**
     * Добавляет новое событие в систему.
     *
     * @param userId    идентификатор пользователя
     * @param eventType тип события
     * @param operation тип операции
     * @param entityId  идентификатор сущности, связанной с событием
     */
    void addEvent(Long userId, EventType eventType, Operation operation, Long entityId);

    /**
     * Возвращает список событий пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список событий
     */
    List<Event> getUserFeed(Long userId);
}