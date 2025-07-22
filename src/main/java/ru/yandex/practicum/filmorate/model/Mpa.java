package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mpa {

    /**
     * Уникальный идентификатор рейтинга MPA.
     */
    @NotNull
    private Integer id;

    /**
     * Название рейтинга MPA.
     */
    @NotNull
    private String name;
}