package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс-модель для создания объекта жанра со свойствами <b>id<b/>, <b>name<b/>.
 */
@Data
@NoArgsConstructor
public class Genre {

    /**
     * Уникальный идентификатор жанра, обязательное поле.
     */
    @NotBlank(message = "ID Жанра должен быть обязательно")
    private Integer id;

    /**
     * Название жанра, обязательное поле.
     */
    @NotBlank(message = "Название жанра обязательно")
    private String name;
}