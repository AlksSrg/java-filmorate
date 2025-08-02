package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * DTO для создания/обновления отзыва.
 */
@Data
public class ReviewDto {
    private Long reviewId;  // Добавьте это поле

    @NotBlank(message = "Содержание отзыва не может быть пустым")
    private String content;

    @NotNull(message = "Тип отзыва не может быть null")
    private Boolean isPositive;

    @NotNull(message = "ID пользователя не может быть null")
    private Long userId;

    @NotNull(message = "ID фильма не может быть null")
    private Long filmId;
}