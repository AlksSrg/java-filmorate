package ru.yandex.practicum.filmorate.storage.dao.like;

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
}