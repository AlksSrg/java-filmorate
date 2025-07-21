package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserDbService;

import java.util.Collection;

/**
 * Класс-контроллер для создания и редактирования пользователей
 */
@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    /**
     * Поле сервис
     */
    private final UserDbService userService;

    /**
     * Добавляет пользователя в хранилище.
     */
    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        User createdUser = userService.getUserStorage().create(user);
        return ResponseEntity.ok(createdUser);
    }

    /**
     * Обновляет пользователя в хранилище.
     */
    @PutMapping
    public ResponseEntity<User> put(@Valid @RequestBody User user) {
        User updatedUser = userService.getUserStorage().updateUser(user);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Добавляет пользователя в друзья.
     */
    @PutMapping("{id}/friends/{friendId}")
    public ResponseEntity<Void> addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.addFriends(id, friendId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Удаляет пользователя из друзей.
     */
    @DeleteMapping("{id}/friends/{friendId}")
    public ResponseEntity<Void> deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.deleteFriends(id, friendId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Запрашивает всех друзей пользователя.
     */
    @GetMapping("{id}/friends")
    public ResponseEntity<Collection<User>> getFriend(@PathVariable Long id) {
        Collection<User> friends = userService.getFriends(id);
        return ResponseEntity.ok(friends);
    }

    /**
     * Запрашивает пользователя по id.
     */
    @GetMapping("{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User foundUser = userService.getUserStorage().getUserById(id);
        return ResponseEntity.ok(foundUser);
    }

    /**
     * Запрашивает общих друзей у двух пользователей.
     */
    @GetMapping("{id}/friends/common/{otherId}")
    public ResponseEntity<Collection<User>> getMutualFriends(@PathVariable Long id, @PathVariable Long otherId) {
        Collection<User> mutualFriends = userService.getMutualFriends(id, otherId);
        return ResponseEntity.ok(mutualFriends);
    }

    /**
     * Запрашивает коллекцию пользователей.
     */
    @GetMapping
    public ResponseEntity<Collection<User>> getUser() {
        Collection<User> allUsers = userService.getUserStorage().getUser();
        return ResponseEntity.ok(allUsers);
    }
}