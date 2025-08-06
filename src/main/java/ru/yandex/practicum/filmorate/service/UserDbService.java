package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.model.constants.EventType;
import ru.yandex.practicum.filmorate.model.constants.Operation;
import ru.yandex.practicum.filmorate.storage.dao.event.EventDao;
import ru.yandex.practicum.filmorate.storage.dao.friends.FriendDao;
import ru.yandex.practicum.filmorate.storage.dao.genre.GenreDao;
import ru.yandex.practicum.filmorate.storage.dao.like.LikeDao;
import ru.yandex.practicum.filmorate.storage.dao.mpa.MpaDao;
import ru.yandex.practicum.filmorate.storage.films.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.utils.ValidationUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для работы с пользователями.
 * Реализует комплексную бизнес-логику, включая:
 * <ul>
 *   <li>CRUD операции с пользователями</li>
 *   <li>Управление дружескими связями</li>
 *   <li>Формирование рекомендаций фильмов</li>
 *   <li>Работу с лентой событий</li>
 * </ul>
 *
 * <p>Использует:</p>
 * <ul>
 *   <li>{@link UserStorage} для хранения пользователей</li>
 *   <li>{@link FriendDao} для управления друзьями</li>
 *   <li>{@link LikeDao} для работы с лайками</li>
 *   <li>{@link FilmDbService} для получения рекомендаций</li>
 * </ul>
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
     * Поле для доступа к операциям с фильмами
     */
    private final FilmStorage filmStorage;

    /**
     * Репозиторий для работы с событиями.
     */
    private final EventDao eventDao;

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
            eventDao.addEvent(userId, EventType.FRIEND, Operation.ADD, idFriend);
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
            eventDao.addEvent(userId, EventType.FRIEND, Operation.REMOVE, idFriend);
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
     * Удаляет пользователя по id.
     *
     * @param id идентификатор пользователя
     */
    public void deleteUserById(long id) {
        userStorage.deleteById(id);
    }


    /**
     * Формирует рекомендации фильмов для пользователя на основе схожих вкусов.
     * Алгоритм:
     * 1. Находит пользователя с максимальным совпадением лайков
     * 2. Рекомендует фильмы, которые понравились этому пользователю, но не текущему
     * 3. Возвращает список, отсортированный по популярности
     *
     * @param id идентификатор пользователя
     * @return список рекомендованных фильмов с полной информацией
     * @throws EntityNotFoundException если пользователь не найден
     */
    public List<Film> getRecommendations(long id) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            throw new EntityNotFoundException("Пользователь не найден");
        }

        Map<Long, Set<Long>> likesMap = likeDao.getAllLikesMap();
        Set<Long> userLikes = likesMap.getOrDefault(id, Collections.emptySet());

        // Если у пользователя нет лайков - возвращаем пустой список
        if (userLikes.isEmpty()) {
            return Collections.emptyList();
        }

        // Находим пользователя с максимальным пересечением лайков
        Optional<Map.Entry<Long, Set<Long>>> bestMatch = likesMap.entrySet().stream()
                .filter(e -> !e.getKey().equals(id)) // Исключаем текущего пользователя
                .filter(e -> !Collections.disjoint(e.getValue(), userLikes)) // Только с пересекающимися лайками
                .max(Comparator.comparingInt(e -> {
                    Set<Long> intersection = new HashSet<>(e.getValue());
                    intersection.retainAll(userLikes);
                    return intersection.size();
                }));

        // Если нет пользователей с пересекающимися лайками - возвращаем пустой список
        if (bestMatch.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> recommendations = new HashSet<>(bestMatch.get().getValue());
        recommendations.removeAll(userLikes);

        // Получаем фильмы и обогащаем их данными
        List<Film> films = filmStorage.getFilmsByIds(recommendations);
        Map<Long, Mpa> mpaMap = mpaDao.getMpaMapByFilms(recommendations);
        Map<Long, Set<Genre>> genresMap = genreDao.getGenresMapByFilms(recommendations);

        for (Film film : films) {
            film.setMpa(mpaMap.get(film.getId()));
            film.setGenres(new TreeSet<>(Comparator.comparing(Genre::getId))); // Сортируем по ID
            film.getGenres().addAll(genresMap.getOrDefault(film.getId(), Collections.emptySet()));
        }

        return films;
    }

    /**
     * Возвращает ленту событий пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список событий друзей пользователя
     */
    public List<Event> getUserFeed(Long userId) {
        // Проверяем существование пользователя
        getUserById(userId);
        return eventDao.getUserFeed(userId);
    }
}