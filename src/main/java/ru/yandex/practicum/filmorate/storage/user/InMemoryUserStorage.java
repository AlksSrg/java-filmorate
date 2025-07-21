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
 * Класс-хранилище реализующий интерфейс для хранения и обновления пользователей со свойствами
 */
@Component("InMemoryUserStorage")
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    /**
     * Поле хранилище пользователей
     */
    private final Map<Long, User> users = new HashMap<>();

    /**
     * Поле счетчик идентификаторов пользователей
     */
    private Long id = 1L;

    /**
     * Метод добавления пользователя.
     */
    public User create(User user) {
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
     * Метод обновления пользователя.
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
     * Получение списка пользователей.
     */
    public Collection<User> getUser() {
        log.debug("Запрошен список пользователей, их количество: {}", users.size());
        return users.values();
    }

    /**
     * Метод получение пользователя по id.
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
}