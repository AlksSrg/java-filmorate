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
 * Сервисный слой для работы с отзывами.
 * Обеспечивает бизнес-логику для операций с отзывами,
 * включая создание, обновление, удаление и оценку отзывов.
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

    /**
     * Создает новый отзыв.
     *
     * @param review объект отзыва
     * @return созданный отзыв
     * @throws EntityNotFoundException если пользователь или фильм не найдены
     */
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

    /**
     * Обновляет существующий отзыв.
     *
     * @param review объект отзыва с обновленными данными
     * @return обновленный отзыв
     * @throws EntityNotFoundException если отзыв не найден
     */
    public Review update(Review review) {
        getById(review.getReviewId()); // проверяем существование
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

    public void addLike(Long reviewId, Long userId) {
        validateUser(userId);
        reviewDao.addLike(reviewId, userId);
    }

    public void addDislike(Long reviewId, Long userId) {
        validateUser(userId);
        reviewDao.addDislike(reviewId, userId);
    }

    /**
     * Удаляет лайк отзыва от пользователя.
     *
     * @param reviewId идентификатор отзыва
     * @param userId   идентификатор пользователя
     * @throws EntityNotFoundException если отзыв или пользователь не найдены
     */
    public void removeLike(Long reviewId, Long userId) {
        validateUser(userId);
        reviewDao.removeLike(reviewId, userId);
    }

    public void removeDislike(Long reviewId, Long userId) {
        validateUser(userId);
        reviewDao.removeDislike(reviewId, userId);
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