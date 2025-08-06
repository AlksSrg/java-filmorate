package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Модель режиссера фильма.
 * Содержит информацию о режиссере, включая идентификатор и имя.
 * Используется для хранения и передачи данных о режиссерах в системе.
 */

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Director {

    /**
     * Уникальный идентификатор режиссера.
     */
    private Long id;

    /**
     * Имя режиссера. Обязательное поле, не может быть пустым или содержать только пробелы.
     */
    @NotBlank
    private String name;
}