package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для работы с хранилищем отзывов.
 * Определяет основные CRUD-операции и методы для работы с лайками/дизлайками.
 */
public interface ReviewDao {
    /**
     * Создает новый отзыв.
     *
     * @param review объект отзыва
     * @return созданный отзыв с присвоенным идентификатором
     */
    Review create(Review review);

    /**
     * Обновляет существующий отзыв.
     *
     * @param review объект отзыва с обновленными данными
     * @return обновленный отзыв
     */
    Review update(Review review);

    /**
     * Удаляет отзыв по идентификатору.
     *
     * @param id идентификатор отзыва
     */
    void delete(Long id);

    /**
     * Находит отзыв по идентификатору.
     *
     * @param id идентификатор отзыва
     * @return Optional с найденным отзывом или пустой, если не найден
     */
    Optional<Review> findById(Long id);

    /**
     * Находит все отзывы для указанного фильма.
     *
     * @param filmId идентификатор фильма
     * @param count  максимальное количество возвращаемых отзывов
     * @return список отзывов, отсортированных по полезности
     */
    List<Review> findByFilmId(Long filmId, int count);

    /**
     * Находит все отзывы с ограничением по количеству.
     *
     * @param count максимальное количество возвращаемых отзывов
     * @return список отзывов, отсортированных по полезности
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

    /**
     * Проверяет, поставил ли пользователь лайк отзыву.
     *
     * @param reviewId идентификатор отзыва
     * @param userId   идентификатор пользователя
     * @return true, если пользователь поставил лайк, иначе false
     */
    boolean hasLike(Long reviewId, Long userId);

    /**
     * Проверяет, поставил ли пользователь дизлайк отзыву.
     *
     * @param reviewId идентификатор отзыва
     * @param userId   идентификатор пользователя
     * @return true, если пользователь поставил дизлайк, иначе false
     */
    boolean hasDislike(Long reviewId, Long userId);
}