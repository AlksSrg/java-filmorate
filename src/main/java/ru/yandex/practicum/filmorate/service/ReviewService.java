package ru.yandex.practicum.filmorate.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.films.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewDao;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

/**
 * Сервис для работы с отзывами.
 */
@Service
public class ReviewService {

    private final ReviewDao reviewDao;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public ReviewService(ReviewDao reviewDao, UserStorage userStorage, FilmStorage filmStorage) {
        this.reviewDao = reviewDao;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public Review create(Review review) {

        if (review.getContent() == null || review.getContent().isBlank()) {
            throw new ValidationException("Содержание отзыва не может быть пустым");
        }
        if (review.getIsPositive() == null) {
            throw new ValidationException("Тип отзыва не может быть null");
        }
        if (review.getUserId() == null) {
            throw new ValidationException("ID пользователя не может быть null");
        }
        if (review.getFilmId() == null) {
            throw new ValidationException("ID фильма не может быть null");
        }

        try {
            userStorage.getUserById(review.getUserId());
            filmStorage.getFilmById(review.getFilmId());
        } catch (EntityNotFoundException e) {
            throw new ValidationException(e.getMessage());
        }

        return reviewDao.create(review);
    }

    public Review update(Review review) {
        getById(review.getReviewId()); // проверяем существование
        return reviewDao.update(review);
    }

    public void delete(Long id) {
        getById(id); // проверяем существование
        reviewDao.delete(id);
    }

    public Review getById(Long id) {
        return reviewDao.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Отзыв с id=" + id + " не найден"));
    }

    public List<Review> getByFilmId(Long filmId, int count) {
        if (filmId != null) {
            filmStorage.getFilmById(filmId); // проверяем существование фильма
        }
        return filmId == null ?
            reviewDao.findAll(count) :
            reviewDao.findByFilmId(filmId, count);
    }

    public void addLike(Long reviewId, Long userId) {
        validateUser(userId);
        reviewDao.addLike(reviewId, userId);
    }

    public void addDislike(Long reviewId, Long userId) {
        validateUser(userId);
        reviewDao.addDislike(reviewId, userId);
    }

    public void removeLike(Long reviewId, Long userId) {
        validateUser(userId);
        reviewDao.removeLike(reviewId, userId);
    }

    public void removeDislike(Long reviewId, Long userId) {
        validateUser(userId);
        reviewDao.removeDislike(reviewId, userId);
    }

    private void validateUserAndFilm(Long userId, Long filmId) {
        userStorage.getUserById(userId);
        filmStorage.getFilmById(filmId);
    }

    private void validateUser(Long userId) {
        userStorage.getUserById(userId);
    }
}