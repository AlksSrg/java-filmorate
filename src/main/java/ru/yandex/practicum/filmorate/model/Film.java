package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.HashSet;

@Data
@NoArgsConstructor
public class Film {

    /**
     * Поле с id фильма
     */
    private Long id;

    /**
     * Поле с названием фильма
     */
    @NotBlank(message = "Название фильма обязательно")
    @Length(min = 1, max = 255, message = "Название фильма должно быть длиной от 1 до 255 символов.")
    private String name;

    /**
     * Поле с описанием фильма
     */
    @NotBlank
    @Size(max = 200, message = "Описание фильма не должно превышать 200 символов")
    private String description;

    /**
     * Поле с датой релиза фильма
     */
    @Past(message = "Дата релиза должна быть позже или равна 28 декабря 1895 года")
    private LocalDate releaseDate;

    /**
     * Продолжительность фильма
     */
    @PositiveOrZero(message = "Продолжительность фильма должна быть неотрицательным числом")
    private Integer duration;

    /**
     * Поле с перечислением жанров фильма
     */
    private HashSet<Genre> genres;

    /**
     * Поле с указанием рейтинга фильма
     */
    @NotNull
    private Mpa mpa;

    /**
     * Конструктор создание нового объекта фильма.
     */
    public Film(String name, String description, LocalDate releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}