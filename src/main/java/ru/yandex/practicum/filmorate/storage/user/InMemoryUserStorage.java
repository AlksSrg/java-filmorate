package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс хранилища пользователей в оперативной памяти.
 */

@Component("InMemoryUserStorage")
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    /**
     * Карта для хранения пользователей (ключ — идентификатор пользователя, значение — объект пользователя).
     */
    private final Map<Long, User> users = new HashMap<>();

    /**
     * Счётчик уникальных идентификаторов пользователей.
     */
    private Long id = 1L;

    /**
     * Добавляет нового пользователя в хранилище.
     *
     * @param user объект пользователя для регистрации
     * @return зарегистрированный пользователь
     * @throws ValidationException если пользователь с таким идентификатором уже существует
     */
    public User addUser(User user) {
        if (users.containsKey(user.getId())) {
            log.debug("Пользователь с данным id уже существует");
            throw new ValidationException(String.format("Пользователь с id %s уже зарегистрирован.", user.getId()));
        }

        log.debug("Пользователь создан");
        user.setId(id++);
        users.put(user.getId(), user);
        return user;
    }

    /**
     * Обновляет информацию о пользователе в хранилище.
     *
     * @param user объект пользователя с обновлёнными данными
     * @return обновлённый пользователь
     * @throws EntityNotFoundException если пользователь с указанным идентификатором не найден
     */
    public User updateUser(User user) {
        Long userId = user.getId();
        if (users.containsKey(userId)) {
            log.debug("Пользователь обновлен");
            users.put(userId, user);
        } else {
            log.debug("Пользователь не существует");
            throw new EntityNotFoundException(String.format("Пользователя с id %s не существует", userId));
        }
        return user;
    }

    /**
     * Возвращает коллекцию всех зарегистрированных пользователей.
     *
     * @return коллекция пользователей
     */
    public Collection<User> getUsers() {
        log.debug("Запрошен список пользователей, их количество: {}", users.size());
        return users.values();
    }

    /**
     * Возвращает пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return объект пользователя
     * @throws EntityNotFoundException если пользователь с указанным идентификатором не найден
     */
    public User getUserById(Long id) {
        if (users.containsKey(id)) {
            log.debug("Запрошен пользователь c id: {}", id);
            return users.get(id);
        } else {
            log.debug("Пользователь не существует");
            throw new EntityNotFoundException(String.format("Пользователя с id %s не существует", id));
        }
    }

    /**
     * Удаляет пользователя по идентификатору.
     * Данная реализация не поддерживает удаление пользователей и выбрасывает исключение.
     *
     * @param id идентификатор пользователя для удаления
     * @throws UnsupportedOperationException всегда, так как метод не поддерживается в данной реализации
     */
    @Override
    public void deleteById(long id) {
        log.warn("Использование устаревшей реализации");
        throw new UnsupportedOperationException("Метод не поддерживается в устаревшей реализации");
    }
}