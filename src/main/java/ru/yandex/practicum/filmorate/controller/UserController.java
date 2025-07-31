package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserDbService;

import java.util.Collection;
import java.util.List;

/**
 * Класс-контроллер для создания и редактирования пользователей
 */
@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    /**
     * Сервис для операций с пользователями.
     */
    private final UserDbService userService;

    /**
     * Создает нового пользователя и сохраняет его в хранилище.
     *
     * @param user объект пользователя для сохранения
     * @return сохраненный объект пользователя
     */
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    /**
     * Обновляет информацию о существующем пользователе.
     *
     * @param user объект пользователя с обновленными данными
     * @return обновленный объект пользователя
     */
    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(user));
    }

    /**
     * Добавляет пользователя в список друзей другого пользователя.
     *
     * @param id       идентификатор текущего пользователя
     * @param friendId идентификатор друга
     * @return успешный ответ без тела
     */
    @PutMapping("{id}/friends/{friendId}")
    public ResponseEntity<Void> addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.addFriends(id, friendId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Удаляет пользователя из списка друзей другого пользователя.
     *
     * @param id       идентификатор текущего пользователя
     * @param friendId идентификатор друга
     * @return успешный ответ без тела
     */
    @DeleteMapping("{id}/friends/{friendId}")
    public ResponseEntity<Void> deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.deleteFriends(id, friendId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Возвращает список друзей пользователя.
     *
     * @param id идентификатор пользователя
     * @return коллекция объектов друзей
     */
    @GetMapping("{id}/friends")
    public ResponseEntity<Collection<User>> getFriends(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getFriends(id));
    }

    /**
     * Возвращает пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return найденный объект пользователя
     */
    @GetMapping("{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Возвращает список общих друзей между двумя пользователями.
     *
     * @param id      идентификатор первого пользователя
     * @param otherId идентификатор второго пользователя
     * @return коллекция общих друзей
     */
    @GetMapping("{id}/friends/common/{otherId}")
    public ResponseEntity<Collection<User>> getMutualFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return ResponseEntity.ok(userService.getMutualFriends(id, otherId));
    }

    /**
     * Возвращает полный список всех зарегистрированных пользователей.
     *
     * @return коллекция всех пользователей
     */
    @GetMapping
    public ResponseEntity<Collection<User>> getUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Удаление пользователя по идентификатору.
     *
     * @param userId уникальный идентификатор фильма
     * @return пустой ответ с успешным статусом
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserById(@PathVariable @Positive Long userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * УВозвращает список рекомендаций фильмов.
     *
     * @param userId уникальный идентификатор фильма
     * @return список рекомендаций
     */
    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable @Positive Long userId) {
        return userService.getRecommendations(userId);
    }

}