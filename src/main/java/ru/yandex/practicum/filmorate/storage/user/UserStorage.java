package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

/**
 * Интерфейс для работы с хранилищем пользователей.
 * Определяет основные CRUD-операции для работы с пользователями.
 */

public interface UserStorage {

    /**
     * Добавляет нового пользователя в хранилище.
     * Присваивает пользователю уникальный идентификатор.
     *
     * @param user объект пользователя для регистрации
     * @return зарегистрированный пользователь с присвоенным идентификатором
     * @throws ValidationException если данные пользователя не проходят валидацию
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

    /**
     * Удаляет пользователя по идентификатору.
     *
     * @param id идентификатор пользователя для удаления
     * @throws EntityNotFoundException если пользователь с указанным идентификатором не найден
     */
    void deleteById(long id);
}