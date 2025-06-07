package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utils.ValidationUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    // Получение списка всех пользователей
    @GetMapping
    public Collection<User> getAllUsers() {
        return users.values();
    }

    // Создание нового пользователя
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User addUser(@Valid @RequestBody User user) {
        try {
            ValidationUtils.validate(user);
            if (user.getName() == null || user.getName().trim().isEmpty()) {
                user.setName(user.getLogin());
            }

            user.setId(getNextUserId());
            users.put(user.getId(), user);
            log.info("Пользователь с именем '{}' успешно добавлен с ID {}", user.getName(), user.getId());
            return user;
        } catch (ValidationException e){
            throw new ValidationException(e.getMessage());
        }
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