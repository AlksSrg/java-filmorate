package ru.yandex.practicum.filmorate.storage.review;

import java.util.List;
import java.util.Optional;
import ru.yandex.practicum.filmorate.model.Review;

/**
 * Интерфейс для работы с хранилищем отзывов.
 */
public interface ReviewDao {

    Review create(Review review);

    Review update(Review review);

    void delete(Long id);

    Optional<Review> findById(Long id);

    List<Review> findByFilmId(Long filmId, int count);

    List<Review> findAll(int count);

    void addLike(Long reviewId, Long userId);

    void addDislike(Long reviewId, Long userId);

    void removeLike(Long reviewId, Long userId);

    void removeDislike(Long reviewId, Long userId);
}