package ru.yandex.practicum.filmorate.storage.dao.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для работы с хранилищем отзывов.
 */

public interface ReviewDao {

    /**
     * Создает новый отзыв.
     *
     * @param review объект отзыва
     * @return сохраненный отзыв
     */
    Review create(Review review);

    /**
     * Обновляет существующий отзыв.
     *
     * @param review объект отзыва
     * @return обновленный отзыв
     */
    Review update(Review review);

    /**
     * Удаляет отзыв по его идентификатору.
     *
     * @param id идентификатор отзыва
     */
    void delete(Long id);

    /**
     * Ищет отзыв по его идентификатору.
     *
     * @param id идентификатор отзыва
     * @return объект отзыва или {@code Optional.empty()} если отзыв не найден
     */
    Optional<Review> findById(Long id);

    /**
     * Возвращает список отзывов для конкретного фильма.
     *
     * @param filmId идентификатор фильма
     * @param count  максимальное количество отзывов
     * @return список отзывов
     */
    List<Review> findByFilmId(Long filmId, int count);

    /**
     * Возвращает ограниченное количество последних отзывов.
     *
     * @param count максимальное количество отзывов
     * @return список отзывов
     */
    List<Review> findAll(int count);

    /**
     * Добавляет лайк отзыву от пользователя.
     *
     * @param reviewId идентификатор отзыва
     * @param userId   идентификатор пользователя
     */
    void addLike(Long reviewId, Long userId);

    /**
     * Добавляет дизлайк отзыву от пользователя.
     *
     * @param reviewId идентификатор отзыва
     * @param userId   идентификатор пользователя
     */
    void addDislike(Long reviewId, Long userId);

    /**
     * Удаляет лайк отзыва от пользователя.
     *
     * @param reviewId идентификатор отзыва
     * @param userId   идентификатор пользователя
     */
    void removeLike(Long reviewId, Long userId);

    /**
     * Удаляет дизлайк отзыва от пользователя.
     *
     * @param reviewId идентификатор отзыва
     * @param userId   идентификатор пользователя
     */
    void removeDislike(Long reviewId, Long userId);
}