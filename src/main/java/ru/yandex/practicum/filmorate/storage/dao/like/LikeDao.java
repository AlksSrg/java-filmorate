package ru.yandex.practicum.filmorate.storage.dao.like;

/**
 * Интерфейс для работы с логикой касающиеся лайков
 */
public interface LikeDao {

    /**
     * Добавление лайка фильму.
     */
    void addLike(Long userId, Long filmId);

    /**
     * Удаление лайка у фильма.
     */
    void deleteLike(Long userId, Long filmId);

    /**
     * Проверяет лайки методом подсчета, и подготавливает данные для компаратора
     */
    int checkLikes(Long filmId);
}