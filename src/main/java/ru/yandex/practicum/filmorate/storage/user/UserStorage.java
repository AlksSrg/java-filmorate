package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

/**
 * Интерфейс для работы с хранилищем пользователей.
 */
public interface UserStorage {

    /**
     * Регистрирует нового пользователя в хранилище.
     *
     * @param user объект пользователя для регистрации
     * @return зарегистрированный пользователь
     */
    User addUser(User user);

    /**
     * Обновляет информацию о пользователе в хранилище.
     *
     * @param user объект пользователя с обновлёнными данными
     * @return обновлённый пользователь
     */
    User updateUser(User user);

    /**
     * Возвращает коллекцию всех зарегистрированных пользователей.
     *
     * @return коллекция пользователей
     */
    Collection<User> getUsers();

    /**
     * Возвращает пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return объект пользователя
     */
    User getUserById(Long id);
}