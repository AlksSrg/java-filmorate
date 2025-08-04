package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * Модель отзыва на фильм.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review {

    @PositiveOrZero
    private Long reviewId;

    @NotBlank(message = "Содержание отзыва не может быть пустым")
    private String content;

    @NotNull(message = "Тип отзыва не может быть null")
    private Boolean isPositive;

    @NotNull(message = "ID пользователя не может быть null")
    private Long userId;

    @NotNull(message = "ID фильма не может быть null")
    private Long filmId;

    private int useful = 0;

    private Set<Long> likes = new HashSet<>();
    private Set<Long> dislikes = new HashSet<>();
}