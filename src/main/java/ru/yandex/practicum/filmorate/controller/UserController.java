package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private final static Logger log = LoggerFactory.getLogger(UserController.class);

    // Получение списка всех пользователей
    @GetMapping
    public Collection<User> getAllUsers() {
        return users.values();
    }

    // Создание нового пользователя
    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        user.setId(getNextUserId());
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Пользователь с именем '{}' успешно добавлен с ID {}", user.getName(), user.getId());
        return user;

    }

    // Обновление пользователя
    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {
        if (!users.containsKey(newUser.getId())) {
            throw new ValidationException("Пользователя с таким ID не существует.");
        }
        users.put(newUser.getId(), newUser);
        log.info("Пользователь с именем '{}' успешно обновлён.", newUser.getName());
        return newUser;
    }

    //Метод для генерации уникально id
    private Long getNextUserId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}