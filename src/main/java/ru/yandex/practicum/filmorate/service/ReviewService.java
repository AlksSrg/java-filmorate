package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.films.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

/**
 * Сервисный слой для работы с отзывами.
 * Обеспечивает бизнес-логику для операций с отзывами,
 * включая создание, обновление, удаление и оценку отзывов.
 */
@Slf4j
@Service
public class ReviewService {
    private final ru.yandex.practicum.filmorate.storage.review.ReviewDao reviewDao;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public ReviewService(ru.yandex.practicum.filmorate.storage.review.ReviewDao reviewDao, UserStorage userStorage, FilmStorage filmStorage) {
        this.reviewDao = reviewDao;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    /**
     * Создает новый отзыв.
     *
     * @param review объект отзыва
     * @return созданный отзыв
     * @throws EntityNotFoundException если пользователь или фильм не найдены
     */
    public Review create(Review review) {
        validateUserAndFilm(review.getUserId(), review.getFilmId());
        log.debug("Создание отзыва: {}", review);
        return reviewDao.create(review);
    }

    /**
     * Обновляет существующий отзыв.
     *
     * @param review объект отзыва с обновленными данными
     * @return обновленный отзыв
     * @throws EntityNotFoundException если отзыв не найден
     */
    public Review update(Review review) {
        getById(review.getReviewId()); // проверяем существование
        log.debug("Обновление отзыва с ID: {}", review.getReviewId());
        return reviewDao.update(review);
    }

    /**
     * Удаляет отзыв по идентификатору.
     *
     * @param id идентификатор отзыва
     * @throws EntityNotFoundException если отзыв не найден
     */
    public void delete(Long id) {
        getById(id); // проверяем существование
        log.debug("Удаление отзыва с ID: {}", id);
        reviewDao.delete(id);
    }

    /**
     * Получает отзыв по идентификатору.
     *
     * @param id идентификатор отзыва
     * @return найденный отзыв
     * @throws EntityNotFoundException если отзыв не найден
     */
    public Review getById(Long id) {
        return reviewDao.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Отзыв с id=" + id + " не найден"));
    }

    /**
     * Получает список отзывов для указанного фильма или все отзывы, если фильм не указан.
     *
     * @param filmId идентификатор фильма (может быть null)
     * @param count  максимальное количество возвращаемых отзывов
     * @return список отзывов, отсортированных по полезности
     */
    public List<Review> getByFilmId(Long filmId, int count) {
        if (filmId != null) {
            filmStorage.getFilmById(filmId); // проверяем существование фильма
        }
        return filmId == null ?
            reviewDao.findAll(count) :
            reviewDao.findByFilmId(filmId, count);
    }

    /**
     * Добавляет лайк отзыву от пользователя.
     *
     * @param reviewId идентификатор отзыва
     * @param userId   идентификатор пользователя
     * @throws EntityNotFoundException если отзыв или пользователь не найдены
     * @throws ValidationException     если пользователь пытается оценить свой собственный отзыв
     */
    public void addLike(Long reviewId, Long userId) {
        Review review = getById(reviewId);
        validateUser(userId);

        if (review.getUserId().equals(userId)) {
            throw new ValidationException("Нельзя оценивать свой собственный отзыв");
        }

        if (reviewDao.hasDislike(reviewId, userId)) {
            reviewDao.removeDislike(reviewId, userId);
        }
        reviewDao.addLike(reviewId, userId);
        log.debug("Добавлен лайк отзыву ID: {} от пользователя ID: {}", reviewId, userId);
    }

    /**
     * Добавляет дизлайк отзыву от пользователя.
     *
     * @param reviewId идентификатор отзыва
     * @param userId   идентификатор пользователя
     * @throws EntityNotFoundException если отзыв или пользователь не найдены
     * @throws ValidationException     если пользователь пытается оценить свой собственный отзыв
     */
    public void addDislike(Long reviewId, Long userId) {
        Review review = getById(reviewId);
        validateUser(userId);

        if (review.getUserId().equals(userId)) {
            throw new ValidationException("Нельзя оценивать свой собственный отзыв");
        }

        if (reviewDao.hasLike(reviewId, userId)) {
            reviewDao.removeLike(reviewId, userId);
        }
        reviewDao.addDislike(reviewId, userId);
        log.debug("Добавлен дизлайк отзыву ID: {} от пользователя ID: {}", reviewId, userId);
    }

    /**
     * Удаляет лайк отзыва от пользователя.
     *
     * @param reviewId идентификатор отзыва
     * @param userId   идентификатор пользователя
     * @throws EntityNotFoundException если отзыв или пользователь не найдены
     */
    public void removeLike(Long reviewId, Long userId) {
        getById(reviewId);
        validateUser(userId);
        reviewDao.removeLike(reviewId, userId);
        log.debug("Удален лайк отзыву ID: {} от пользователя ID: {}", reviewId, userId);
    }

    /**
     * Удаляет дизлайк отзыва от пользователя.
     *
     * @param reviewId идентификатор отзыва
     * @param userId   идентификатор пользователя
     * @throws EntityNotFoundException если отзыв или пользователь не найдены
     */
    public void removeDislike(Long reviewId, Long userId) {
        getById(reviewId);
        validateUser(userId);
        reviewDao.removeDislike(reviewId, userId);
        log.debug("Удален дизлайк отзыву ID: {} от пользователя ID: {}", reviewId, userId);
    }

    /**
     * Проверяет существование пользователя и фильма.
     *
     * @param userId идентификатор пользователя
     * @param filmId идентификатор фильма
     * @throws EntityNotFoundException если пользователь или фильм не найдены
     */
    private void validateUserAndFilm(Long userId, Long filmId) {
        userStorage.getUserById(userId);
        filmStorage.getFilmById(filmId);
    }

    /**
     * Проверяет существование пользователя.
     *
     * @param userId идентификатор пользователя
     * @throws EntityNotFoundException если пользователь не найден
     */
    private void validateUser(Long userId) {
        userStorage.getUserById(userId);
    }
}