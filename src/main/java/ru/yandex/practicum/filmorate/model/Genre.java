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
     * Поле идентификатор жанра
     */
    @NotBlank(message = "ID Жанра должен быть обязательно")
    Integer id;

    /**
     * Поле содержащие имя жанра
     */
    @NotBlank(message = "Название жанра обязательно")
    String name;
}