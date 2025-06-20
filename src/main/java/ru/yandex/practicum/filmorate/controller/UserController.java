package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    // Получение пользователя по Id
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // Получение списка всех пользователей
    @GetMapping
    public Collection<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Создание нового пользователя
    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        return userService.addUser(user);
    }

    // Обновление пользователя
    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {
        return userService.updateUser(newUser);
    }

    // Добавление пользователя в друзья
    @PutMapping("/{userId}/friends/{friendId}")
    public void addFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        userService.addFriend(userId, friendId);
    }

    // Удаление пользователя из друзей
    @DeleteMapping("/{userId}/friends/{friendId}")
    public void removeFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        userService.removeFriend(userId, friendId);
    }

    // Получение списка друзей по Id пользователя
    @GetMapping("/{userId}/friends")
    public Collection<User> getAllFriend(@PathVariable Long userId) {
        return userService.getFriends(userId);
    }

    // Получение списка общих друзей
    @GetMapping("/{firstUserId}/friends/common/{secondUserId}")
    public Collection<User> getCommonFriends(@PathVariable Long firstUserId, @PathVariable Long secondUserId) {
        return userService.getCommonFriends(firstUserId, secondUserId);
    }

}