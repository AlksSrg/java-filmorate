package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.utils.constants.EventType;
import ru.yandex.practicum.filmorate.utils.constants.Operation;

/**
 * Модель события в системе.
 * Представляет действия пользователей (лайки, отзывы, дружба) для формирования ленты событий.
 * Содержит информацию о типе события, пользователе, времени и связанной сущности.
 */

@Data
@Builder
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
     * Тип события.
     */
    private EventType eventType;

    /**
     * Тип операции.
     */
    private Operation operation;

    /**
     * Идентификатор сущности, к которой относится событие.
     */
    private Long entityId;
}