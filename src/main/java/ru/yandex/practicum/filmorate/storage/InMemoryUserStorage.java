package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Getter
@Setter
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private long id = 0;

    //Получение пользователя по Id
    @Override
    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    //Получение списка всех пользователей
    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    //Добавление нового пользователя
    @Override
    public User addUser(User user) {
        user.setId(getNextUserId());
        users.put(user.getId(), user);
        return user;
    }

    //Обновление пользователя
    @Override
    public Optional<User> updateUser(User newUser) {
        return Optional.ofNullable(users.computeIfPresent(newUser.getId(), (k, v) -> newUser));
    }

    // Добавление пользователя в друзья
    @Override
    public void addFriend(Long userId, Long friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);
        if (user != null && friend != null) {
            user.getFriends().add(friendId);
        }
    }

    // Удаление пользователя из друзей
    @Override
    public void removeFriend(Long userId, Long friendId) {
        User user = users.get(userId);
        if (user != null) {
            user.getFriends().remove(friendId);
        }
    }

    // Получение списка друзей по ID пользователя
    @Override
    public Collection<User> getFriends(Long userId) {
        User user = users.get(userId);
        if (user != null) {
            return user.getFriends().stream()
                    .map(friendId -> users.get(friendId))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    // Получение списка общих друзей двух пользователей
    @Override
    public Collection<User> getCommonFriends(Long firstUserId, Long secondUserId) {
        User firstUser = users.get(firstUserId);
        User secondUser = users.get(secondUserId);
        if (firstUser != null && secondUser != null) {
            Set<Long> commonFriends = new HashSet<>(firstUser.getFriends());
            commonFriends.retainAll(secondUser.getFriends());
            return commonFriends.stream()
                    .map(friendId -> users.get(friendId))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
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