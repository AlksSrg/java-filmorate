package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.friends.FriendDao;
import ru.yandex.practicum.filmorate.storage.dao.genre.GenreDao;
import ru.yandex.practicum.filmorate.storage.dao.like.LikeDao;
import ru.yandex.practicum.filmorate.storage.dao.mpa.MpaDao;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Класс-сервис с логикой для работы с пользователями
 */
@Getter
@Service
@RequiredArgsConstructor
@Slf4j
public class UserDbService {

    /**
     * Поле с прошлой версией хранилища пользователей
     */
    private final UserStorage userStorage;
    /**
     * Поле для доступа к операциям с жанрами
     */
    private final GenreDao genreDao;
    /**
     * Поле для доступа к операциям с рейтингом
     */
    private final MpaDao mpaDao;
    /**
     * Поле для доступа к операциям с лайками
     */
    private final LikeDao likeDao;
    /**
     * Поле для доступа к операциям с друзьями
     */
    private final FriendDao friendDao;

    /**
     * Конструктор сервиса.
     */
    @Autowired
    public UserDbService(@Qualifier("UserDbStorage") UserDbStorage userStorage,
                         GenreDao genreDao,
                         MpaDao mpaDao,
                         LikeDao likeDao,
                         FriendDao friendDao) {

        this.userStorage = userStorage;
        this.genreDao = genreDao;
        this.mpaDao = mpaDao;
        this.likeDao = likeDao;
        this.friendDao = friendDao;
    }

    /**
     * Добавление в друзья.
     */
    public void addFriends(Long userId, Long idFriend) {
        if (userId > 0 && idFriend > 0) {
            boolean status = friendDao.statusFriend(userId, idFriend);
            friendDao.addFriends(userId, idFriend, status);
            log.info("Пользователи с id {} и {} добавлены друг другу в друзья", userId, idFriend);
        } else {
            throw new EntityNotFoundException(String.format("Введен не верный id пользователя %s или друга %s", userId, idFriend));
        }
    }

    /**
     * Удаление из друзей.
     */
    public void deleteFriends(Long userId, Long idFriend) {
        log.info("Пользователь {} удаляет из друзей пользователя {}.", userId, idFriend);

        // Проверяем существование пользователя и друга
        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(userId));
        Optional<User> friendOptional = Optional.ofNullable(userStorage.getUserById(idFriend));

        if (userOptional.isPresent() && friendOptional.isPresent()) {
            // Выполняем удаление дружбы
            friendDao.deleteFriends(userId, idFriend);
            log.info("Пользователь с id {} и {} удалены друг у друга из друзей", userId, idFriend);
        } else {
            // Если пользователь или друг не найден, бросаем исключение
            throw new EntityNotFoundException("Один из пользователей не найден");
        }
    }

    /**
     * Получение списка общих друзей у двух пользователей.
     */
    public List<User> getMutualFriends(Long userId, Long idFriend) {
        List<User> userFriends = getFriends(userId);
        List<User> friendFriends = getFriends(idFriend);

        log.info("Запрошены общие друзья у пользователя с id {} и {}", userId, idFriend);

        return friendFriends.stream()
                .filter(userFriends::contains)
                .filter(friendFriends::contains)
                .collect(Collectors.toList());

    }

    /**
     * Получение списка друзей у пользователя.
     */
    public List<User> getFriends(Long id) {
        if (userStorage.getUserById(id).getEmail().isEmpty()) {

            throw new EntityNotFoundException(String.format("Пользователь с id %s не существует", id));

        }
        log.info("Запрошены друзья у пользователя с id {}", id);

        return friendDao.getFriend(id).stream()
                .mapToLong(Long::valueOf)
                .mapToObj(userStorage::getUserById)
                .collect(Collectors.toList());
    }
}