package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Film {
    private Long id;

    @NotBlank(message = "Название фильма обязательно")
    private String name;

    @Size(max = 200, message = "Длина описания фильма не должна превышать 200 символов")
    private String description;

    @Past(message = "Дата релиза должна быть позже или равна 28 декабря 1895 года")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private Integer duration;

}