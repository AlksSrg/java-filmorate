package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.constants.EventType;
import ru.yandex.practicum.filmorate.model.constants.Operation;
import ru.yandex.practicum.filmorate.storage.dao.event.EventDao;
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
    private final EventDao eventDao;

    @Autowired
    public ReviewService(ReviewDao reviewDao, UserStorage userStorage, FilmStorage filmStorage, EventDao eventDao) {
        this.reviewDao = reviewDao;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.eventDao = eventDao;
    }

    public Review create(Review review) {
        validateUserAndFilm(review.getUserId(), review.getFilmId());
        Review createdReview = reviewDao.create(review);
        eventDao.addEvent(review.getUserId(), EventType.REVIEW, Operation.ADD, createdReview.getReviewId());
        return createdReview;
    }

    public Review update(Review review) {
        getById(review.getReviewId());
        Review updatedReview = reviewDao.update(review);
        eventDao.addEvent(review.getUserId(), EventType.REVIEW, Operation.UPDATE, updatedReview.getReviewId());
        return updatedReview;
    }

    public void delete(Long id) {
        Review review = getById(id);
        reviewDao.delete(id);
        eventDao.addEvent(review.getUserId(), EventType.REVIEW, Operation.REMOVE, id);
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