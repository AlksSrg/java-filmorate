package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Friend {

    /**
     * Идентификатор пользователя.
     */
    private Long userId;

    /**
     * Идентификатор друга пользователя.
     */
    private Long friendId;

    /**
     * Статус отношений:
     * true — дружеские отношения установлены,
     * false — отправлен запрос на установление дружбы.
     */
    boolean status;
}