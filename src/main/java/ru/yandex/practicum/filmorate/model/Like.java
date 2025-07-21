package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Like {

    /**
     * Поле содержащие айди фильма
     */
    Long filmId;

    /**
     * Поле содержащие айди пользователя
     */
    Long userId;
}