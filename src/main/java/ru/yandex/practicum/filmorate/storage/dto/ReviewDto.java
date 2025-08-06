package ru.yandex.practicum.filmorate.storage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;


/**
 * DTO для создания/обновления отзыва.
 */

@Data
public class ReviewDto {
    @Positive(message = "ID отзыва должен быть положительным числом")
    private Long reviewId;

    @NotBlank(message = "Содержание отзыва не может быть пустым")
    private String content;

    @NotNull(message = "Тип отзыва не может быть null")
    private Boolean isPositive;

    @NotNull(message = "ID пользователя не может быть null")
    private Long userId;

    @NotNull(message = "ID фильма не может быть null")
    private Long filmId;
}