package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
     * Имя режиссера обязательно для заполнения.
     */
    @NotBlank
    private String name;
}