package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Friend {

    /**
     * Поле идентификатор пользователя
     */
    Long userId;

    /**
     * Поле идентификатор друга пользователя
     */
    Long friendId;

    /**
     * Поле статуса дружбы(true - принятая дружба, false - есть запрос на дружбу)
     */
    boolean status;
}