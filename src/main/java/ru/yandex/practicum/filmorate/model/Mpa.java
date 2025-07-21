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
     * Поле с идентификатором рейтинга.
     */
    @NotNull
    private Integer id;

    /**
     * Поле именем рейтинга.
     */
    @NotNull
    private String name;
}