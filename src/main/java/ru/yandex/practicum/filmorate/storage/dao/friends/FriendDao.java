package ru.yandex.practicum.filmorate.storage.dao.friends;

import java.util.List;

/**
 * Интерфейс для работы с логикой дружбы между пользователями.
 */
public interface FriendDao {

    /**
     * Добавляет пользователя в список друзей другому пользователю.
     *
     * @param userId   идентификатор пользователя, который добавляет в друзья
     * @param idFriend идентификатор пользователя, которого добавляют в друзья
     * @param status   состояние дружбы (true — подтвержденная дружба, false — отправлена заявка на дружбу)
     */
    void addFriends(Long userId, Long idFriend, boolean status);

    /**
     * Удаляет пользователя из списка друзей.
     *
     * @param userId   идентификатор пользователя, который удаляет из друзей
     * @param idFriend идентификатор пользователя, которого удаляют из друзей
     */
    void deleteFriends(Long userId, Long idFriend);

    /**
     * Определяет статус взаимоотношений между двумя пользователями.
     *
     * @param userId   идентификатор первого пользователя
     * @param friendId идентификатор второго пользователя
     * @return true, если пользователи являются друзьями, иначе false
     */
    boolean statusFriend(Long userId, Long friendId);

    /**
     * Возвращает список идентификаторов друзей пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список идентификаторов друзей
     */
    List<Long> getFriends(Long userId);
}