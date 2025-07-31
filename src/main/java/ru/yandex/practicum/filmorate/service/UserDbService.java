package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.friends.FriendDao;
import ru.yandex.practicum.filmorate.storage.dao.genre.GenreDao;
import ru.yandex.practicum.filmorate.storage.dao.like.LikeDao;
import ru.yandex.practicum.filmorate.storage.dao.mpa.MpaDao;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.utils.ValidationUtils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сервис для обработки запросов, связанных с пользователями.
 */
@Getter
@Service
@RequiredArgsConstructor
@Slf4j
public class UserDbService {

    /**
     * Хранилище пользователей.
     */
    private final UserStorage userStorage;
    /**
     * Репозиторий для работы с жанрами.
     */
    private final GenreDao genreDao;
    /**
     * Репозиторий для работы с рейтингами MPA.
     */
    private final MpaDao mpaDao;
    /**
     * Репозиторий для работы с лайками.
     */
    private final LikeDao likeDao;
    /**
     * Репозиторий для работы с друзьями.
     */
    private final FriendDao friendDao;
    /**
     * Поле для доступа к операциям с фильмами
     */
    private final FilmDbService filmService;

    /**
     * Регистрирует нового пользователя.
     *
     * @param user объект пользователя для регистрации
     * @return зарегистрированный пользователь
     */
    public User createUser(User user) {
        log.info("Добавление пользователя c именем {} в базу данных.", user.getName());
        ValidationUtils.validateUser(user);
        return userStorage.addUser(user);
    }

    /**
     * Обновляет информацию о пользователе.
     *
     * @param user объект пользователя с обновлёнными данными
     * @return обновлённый пользователь
     */
    public User updateUser(User user) {
        log.info("Обновление пользователя c именем {} в базе данных.", user.getName());
        if (userStorage.getUserById(user.getId()) == null) {
            throw new EntityNotFoundException("Такого пользователя не существует");
        }
        return userStorage.updateUser(user);
    }

    /**
     * Возвращает пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return объект пользователя
     */
    public User getUserById(Long id) {
        log.info("Получение пользователя c id {} из базе данных.", id);
        return userStorage.getUserById(id);
    }

    /**
     * Добавляет пользователя в список друзей другому пользователю.
     *
     * @param userId   идентификатор пользователя, который добавляет в друзья
     * @param idFriend идентификатор пользователя, которого добавляют в друзья
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
     * Удаляет пользователя из списка друзей.
     *
     * @param userId   идентификатор пользователя, который удаляет из друзей
     * @param idFriend идентификатор пользователя, которого удаляют из друзей
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
     * Возвращает список общих друзей у двух пользователей.
     *
     * @param userId   идентификатор первого пользователя
     * @param idFriend идентификатор второго пользователя
     * @return список общих друзей
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
     * Возвращает список друзей пользователя.
     *
     * @param id идентификатор пользователя
     * @return список друзей пользователя
     */
    public List<User> getFriends(Long id) {
        if (userStorage.getUserById(id).getEmail().isEmpty()) {

            throw new EntityNotFoundException(String.format("Пользователь с id %s не существует", id));

        }
        log.info("Запрошены друзья у пользователя с id {}", id);

        return friendDao.getFriends(id).stream()
                .mapToLong(Long::valueOf)
                .mapToObj(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    /**
     * Возвращает коллекцию всех зарегистрированных пользователей.
     *
     * @return коллекция объектов {@link User}, хранящихся в базе данных
     */
    public Collection<User> getAllUsers() {
        log.info("Запрошены все пользователи из базы данных.");
        return userStorage.getUsers();
    }

    /**
     * Удаление пользователя по id.
     *
     * @param id идентификатор пользователя
     */
    public void deleteUserById(long id) {
        userStorage.deleteById(id);
    }


    /**
     * Метод предоставляет рекомендуемые фильмы для пользователя.
     * Точность таргета зависит от активности пользователя.
     *
     * @param id id пользователя для которого запрашиваются рекомендации.
     * @return возвращает список рекомендуемых фильмов или пустой список если таргет недостаточно обогащен.
     * @throws EntityNotFoundException генерирует ошибку в случае если пользователь не зарегистрирован.
     */
    public List<Film> getRecommendations(long id) {
        if (userStorage.getUserById(id) == null) {
            throw new EntityNotFoundException(String.format("пользователь с id %d не зарегистрирован.", id));
        } else {
            log.info("Запрошены рекомендации для пользователя с id {}", id);
            final Collection<Film> userFilms = filmService.getFilmsByUser(id);
            long userId = 0;
            long countCoincidences = 0;
            for (User user : userStorage.getUsers()) {
                if (user.getId() != id) {
                    long count = 0;
                    for (Film film : filmService.getFilmsByUser(user.getId())) {
                        if (userFilms.contains(film)) {
                            count++;
                        }
                    }
                    if (count > countCoincidences) {
                        userId = user.getId();
                        countCoincidences = count;
                    }
                }
            }
            log.info("Рекомендации для пользователя с id {} успешно предоставлены", id);
            return filmService.getFilmsByUser(userId).stream()
                    .filter(film -> !userFilms.contains(film))
                    .collect(Collectors.toList());
        }
    }

}