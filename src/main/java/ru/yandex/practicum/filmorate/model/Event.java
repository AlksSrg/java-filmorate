package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.constants.EventType;
import ru.yandex.practicum.filmorate.model.constants.Operation;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    private Long eventId; // Уникальный идентификатор события.

    private Long timestamp; // Временная метка события в миллисекундах.

    private Long userId; // Идентификатор пользователя, совершившего действие.

    private EventType eventType; // Тип события (LIKE, REVIEW, FRIEND).

    private Operation operation; // Тип операции (ADD, REMOVE, UPDATE).

    private Long entityId; // Идентификатор сущности, с которой произошло событие.
}
