package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.Set;

/**
 * Модель фильма.
 * Основная сущность системы, содержащая информацию о фильме:
 * название, описание, дата выпуска, продолжительность и связанные данные.
 * Поддерживает валидацию основных полей.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Film {

    /**
     * Уникальный идентификатор фильма.
     */
    private Long id;

    /**
     * Название фильма, обязательное для заполнения, длина от 1 до 255 символов.
     */
    @NotBlank(message = "Название фильма обязательно")
    @Length(min = 1, max = 255, message = "Название фильма должно быть длиной от 1 до 255 символов.")
    private String name;

    /**
     * Описание фильма. Обязательное поле, максимум 200 символов.
     */
    @NotBlank
    @Size(max = 200, message = "Описание фильма не должно превышать 200 символов")
    private String description;

    /**
     * Дата выпуска фильма.
     */
    private LocalDate releaseDate;

    /**
     * Продолжительность фильма в минутах. Должна быть неотрицательной.
     */
    @PositiveOrZero(message = "Продолжительность фильма должна быть неотрицательным числом")
    private Integer duration;

    /**
     * Набор жанров фильма.
     */
    private Set<Genre> genres;

    /**
     * Возрастной рейтинг MPA фильма. Обязательное поле.
     */
    @NotNull
    private Mpa mpa;

    /**
     * Список режиссёров фильма.
     */
    private Set<Director> directors;
}