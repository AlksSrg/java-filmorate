package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.dao.review.ReviewDao;
import ru.yandex.practicum.filmorate.storage.films.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

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
        validateUserAndFilm(review.getUserId(), review.getFilmId());
        return reviewDao.create(review);
    }

    public Review update(Review review) {
        getById(review.getReviewId());
        return reviewDao.update(review);
    }

    public void delete(Long id) {
        getById(id);
        reviewDao.delete(id);
    }

    public Review getById(Long id) {
        return reviewDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Отзыв с id=" + id + " не найден"));
    }

    public List<Review> getByFilmId(Long filmId, int count) {
        if (filmId != null) {
            filmStorage.getFilmById(filmId);
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