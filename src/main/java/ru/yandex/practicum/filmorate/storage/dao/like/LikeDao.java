package ru.yandex.practicum.filmorate.storage.dao.like;

import java.util.Map;
import java.util.Set;

/**
 * Интерфейс для работы с лайками фильмов.
 */
public interface LikeDao {

    /**
     * Добавляет лайк фильму от определенного пользователя.
     *
     * @param userId идентификатор пользователя
     * @param filmId идентификатор фильма
     */
    void addLike(Long userId, Long filmId);

    /**
     * Удаляет лайк у фильма от определенного пользователя.
     *
     * @param userId идентификатор пользователя
     * @param filmId идентификатор фильма
     */
    void deleteLike(Long userId, Long filmId);

    /**
     * Подсчитывает количество лайков у фильма.
     *
     * @param filmId идентификатор фильма
     * @return количество лайков у фильма
     */
    int checkLikes(Long filmId);

    /**
     * Получение карты из БД с фильмами у которых стоят лайки.
     *
     * @return Возвращает карту, где ключом является идентификатор пользователя,
     * а значением - множество идентификаторов фильмов, которым пользователь поставил лайк.
     */
    Map<Long, Set<Long>> getAllLikesMap();

    /**
     * Возвращает ID фильмов, которым пользователь поставил лайк
     *
     * @param userId идентификатор пользователя
     * @return множество ID фильмов
     */
    Set<Long> getLikedFilms(Long userId);

    /**
     * Возвращает количество лайков у фильма
     *
     * @param filmId идентификатор фильма
     * @return количество лайков
     */
    int getLikesCount(Long filmId);
}