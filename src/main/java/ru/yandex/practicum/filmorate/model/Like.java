package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Like {

    /**
     * Идентификатор фильма, которому поставлен лайк.
     */
    private Long filmId;

    /**
     * Идентификатор пользователя, поставившего лайк.
     */
    private Long userId;
}