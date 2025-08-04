package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
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
     * Описание фильма, обязательное для заполнения, максимум 200 символов.
     */
    @NotBlank
    @Size(max = 200, message = "Описание фильма не должно превышать 200 символов")
    private String description;

    /**
     * Дата выпуска фильма.
     */
    private LocalDate releaseDate;

    /**
     * Длительность фильма в минутах, значение должно быть неотрицательным.
     */
    @PositiveOrZero(message = "Продолжительность фильма должна быть неотрицательным числом")
    private Integer duration;

    /**
     * Набор жанров, к которым относится фильм.
     */
    private Set<Genre> genres;

    /**
     * Рейтинг MPA фильма, обязательное поле.
     */
    @NotNull
    private Mpa mpa;

    /**
     * Список режиссёров фильма.
     */
    private Set<Director> directors;

    /**
     * Конструктор для инициализации объекта фильма основными полями.
     *
     * @param name        название фильма
     * @param description описание фильма
     * @param releaseDate дата выхода фильма
     * @param duration    продолжительность фильма
     */
    public Film(String name, String description, LocalDate releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}