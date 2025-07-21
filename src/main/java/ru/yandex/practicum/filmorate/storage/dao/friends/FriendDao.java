package ru.yandex.practicum.filmorate.storage.dao.friends;

import java.util.List;

/**
 * Интерфейс для работы с логикой касающиеся дружбы
 */
public interface FriendDao {

    /**
     * Добавление в друзья.
     */
    void addFriends(Long userId, Long idFriend, boolean status);

    /**
     * Удаление из друзей.
     */
    void deleteFriends(Long userId, Long idFriend);

    /**
     * Получение статуса пользователя
     */
    boolean statusFriend(Long userId, Long friendId);

    /**
     * Получение списка друзей у пользователя.
     */
    List<Long> getFriend(Long userId);
}