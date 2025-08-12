package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Модель жанра фильма.
 * Содержит идентификатор и название жанра.
 * Используется для категоризации фильмов.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Genre {

    /**
     * Уникальный идентификатор жанра. Обязательное поле.
     */
    @NotNull(message = "ID жанра обязательно должен быть указан")
    private Integer id;

    /**
     * Название жанра. Обязательное поле, не может быть пустым.
     */
    @NotBlank(message = "Название жанра обязательно")
    private String name;
}