package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Модель лайка фильма.
 * Связывает пользователя и фильм, которому был поставлен лайк.
 */

@Data
@NoArgsConstructor
public class Like {

    /**
     * Идентификатор фильма. Должен быть положительным числом.
     */
    @Positive(message = "ID фильма должен быть положительным числом")
    private Long filmId;

    /**
     * Идентификатор пользователя. Должен быть положительным числом.
     */
    @Positive(message = "ID пользователя должен быть положительным числом")
    private Long userId;
}