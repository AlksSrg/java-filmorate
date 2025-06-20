package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    // Получение списка всех пользователей
    public Collection<User> getAllUsers() {
        log.info("Запрашивается полный список пользователей.");
        return userStorage.getAllUsers();
    }

    // Получение пользователя по ID
    public User getUserById(Long userId) {
        log.info("Запрашивается пользователь с ID={}.", userId);
        return userStorage.findUserById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с таким Id (%d) не найден".formatted(userId)));
    }

    // Создание нового пользователя
    public User addUser(User user) {
        log.debug("Добавление нового пользователя.");
        try {
            if (user.getName() == null || user.getName().trim().isEmpty()) {
                user.setName(user.getLogin());
            }
            userStorage.addUser(user);
            log.info("Пользователь с именем '{}' успешно добавлен с ID {}", user.getName(), user.getId());
            return user;
        } catch (ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    // Обновление пользователя
    public User updateUser(User user) {
        return userStorage.updateUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с таким Id (%d) не найден".formatted(user.getId())));
    }

    // Добавление пользователя в друзья
    public void addFriend(Long userId, Long friendId) {
        if (userStorage.findUserById(userId).isEmpty() || userStorage.findUserById(friendId).isEmpty()) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
        userStorage.addFriend(userId, friendId);
        userStorage.addFriend(friendId, userId);
        log.info("Пользователь {} стал другом пользователя {}", userStorage.findUserById(userId), userStorage.findUserById(friendId));
    }

    // Удаление пользователя из друзей
    public void removeFriend(Long userId, Long friendId) {
        log.info("Пользователь {} удаляет из друзей пользователя {}.", userId, friendId);

        // Проверяем существование пользователя
        if (userStorage.findUserById(userId).isEmpty()) {
            throw new EntityNotFoundException("Пользователь с ID " + userId + " не найден.");
        }

        // Проверяем существование друга
        if (userStorage.findUserById(friendId).isEmpty()) {
            throw new EntityNotFoundException("Пользователь с ID " + friendId + " не найден.");
        }

        // Удаляем друга из списка друзей пользователя
        userStorage.removeFriend(userId, friendId);
        userStorage.removeFriend(friendId, userId);
    }

    // Возврат списка друзей пользователя
    public Collection<User> getFriends(Long userId) {
        log.info("Запрашиваются друзья пользователя с ID={}", userId);
        if (userStorage.findUserById(userId).isEmpty()) {
            throw new EntityNotFoundException("Список друзей пользователя пока пуст");
        }
        return userStorage.getFriends(userId);
    }

    // Список общих друзей двух пользователей
    public Collection<User> getCommonFriends(Long firstUserId, Long secondUserId) {
        log.info("Запрашиваются общие друзья пользователей с ID={},{}", firstUserId, secondUserId);
        return userStorage.getCommonFriends(firstUserId, secondUserId);
    }
}