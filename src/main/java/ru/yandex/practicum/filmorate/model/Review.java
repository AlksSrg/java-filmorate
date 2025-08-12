package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * Модель отзыва на фильм.
 * Содержит текст отзыва, оценку полезности и информацию об авторе.
 * Поддерживает систему лайков/дизлайков для оценки полезности отзывов.
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Review {

    /**
     * Уникальный идентификатор отзыва. Должен быть неотрицательным.
     */
    @PositiveOrZero(message = "ID отзыва должен быть неотрицательным")
    private Long reviewId;

    /**
     * Текст отзыва. Не может быть пустым.
     */
    @NotBlank(message = "Содержание отзыва не может быть пустым")
    private String content;

    /**
     * Флаг положительности отзыва. Не может быть null.
     */
    @NotNull(message = "Тип отзыва не может быть null")
    private Boolean isPositive;

    /**
     * Идентификатор автора отзыва. Должен быть положительным.
     */
    @Positive(message = "ID пользователя должен быть положительным числом")
    private Long userId;

    /**
     * Идентификатор фильма. Должен быть положительным.
     */
    @Positive(message = "ID фильма должен быть положительным числом")
    private Long filmId;

    /**
     * Показатель полезности отзыва, рассчитывается как:
     * (количество лайков) - (количество дизлайков).
     * Может быть отрицательным, если дизлайков больше.
     */
    private int useful = 0;

    /**
     * Множество идентификаторов пользователей, поставивших лайк.
     */
    private Set<Long> likes = new HashSet<>();

    /**
     * Множество идентификаторов пользователей, поставивших дизлайк.
     */
    private Set<Long> dislikes = new HashSet<>();
}