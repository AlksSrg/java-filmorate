package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.constants.EventType;
import ru.yandex.practicum.filmorate.model.constants.Operation;

/**
 * Модель события в системе.
 * Представляет действия пользователей (лайки, отзывы, дружба) для формирования ленты событий.
 * Содержит информацию о типе события, пользователе, времени и связанной сущности.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    /**
     * Уникальный идентификатор события.
     */
    private Long eventId;

    /**
     * Временная метка события в миллисекундах.
     */
    private Long timestamp;

    /**
     * Идентификатор пользователя, совершившего действие.
     */
    private Long userId;

    /**
     * Тип события (LIKE, REVIEW, FRIEND).
     */
    private EventType eventType;

    /**
     * Тип операции (ADD, REMOVE, UPDATE).
     */
    private Operation operation;

    /**
     * Идентификатор сущности, к которой относится событие.
     */
    private Long entityId;
}