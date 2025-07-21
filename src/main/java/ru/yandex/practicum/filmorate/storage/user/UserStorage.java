package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

/**
 * Интерфейс для работы с хранилищем пользователей
 */
public interface UserStorage {

    /**
     * Метод добавления пользователя
     */
    User create(User user);

    /**
     * Метод обновления пользователя
     */
    User updateUser(User user);

    /**
     * Метод запроса пользователей
     */
    Collection<User> getUser();

    /**
     * Метод запроса пользователя по id
     */
    User getUserById(Long id);
}