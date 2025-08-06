package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Модель возрастного рейтинга MPA (Motion Picture Association).
 * Определяет возрастные ограничения для фильмов.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mpa {

    /**
     * Уникальный идентификатор рейтинга. Обязательное поле.
     */
    @NotNull
    private Integer id;

    /**
     * Название рейтинга. Обязательное поле.
     */
    @NotNull
    private String name;
}