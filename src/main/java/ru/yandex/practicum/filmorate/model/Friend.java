package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Модель дружеской связи между пользователями.
 * Содержит информацию о пользователях и статусе их дружбы.
 */

@Data
@NoArgsConstructor
public class Friend {

    /**
     * Идентификатор пользователя. Должен быть положительным числом.
     */
    @Positive(message = "ID пользователя должен быть положительным числом")
    private Long userId;

    /**
     * Идентификатор друга пользователя. Должен быть положительным числом.
     */
    @Positive(message = "ID друга должен быть положительным числом")
    private Long friendId;

    /**
     * Статус дружбы между пользователями:
     * true - дружба подтверждена (взаимная),
     * false - запрос на дружбу ожидает подтверждения.
     */
    boolean status;
}