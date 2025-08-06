package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.constants.EventType;
import ru.yandex.practicum.filmorate.model.constants.Operation;
import ru.yandex.practicum.filmorate.storage.dao.event.EventDao;
import ru.yandex.practicum.filmorate.storage.dao.review.ReviewDao;
import ru.yandex.practicum.filmorate.storage.films.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

/**
 * Сервис для работы с отзывами на фильмы.
 * Реализует бизнес-логику для:
 * <ul>
 *   <li>Создания, обновления и удаления отзывов</li>
 *   <li>Управления оценками отзывов (лайки/дизлайки)</li>
 *   <li>Получения отзывов по различным критериям</li>
 * </ul>
 *
 * <p>Использует:</p>
 * <ul>
 *   <li>{@link ReviewDao} для хранения отзывов</li>
 *   <li>{@link UserStorage} и {@link FilmStorage} для валидации</li>
 *   <li>{@link EventDao} для логирования событий</li>
 * </ul>
 */

@Service
public class ReviewService {

    private final ReviewDao reviewDao;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final EventDao eventDao;

    /**
     * Конструктор сервиса отзывов с инъекцией зависимостей.
     *
     * @param reviewDao   хранилище отзывов
     * @param userStorage хранилище пользователей
     * @param filmStorage хранилище фильмов
     * @param eventDao    хранилище событий
     */
    @Autowired
    public ReviewService(ReviewDao reviewDao, UserStorage userStorage, FilmStorage filmStorage, EventDao eventDao) {
        this.reviewDao = reviewDao;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.eventDao = eventDao;
    }

    /**
     * Создает новый отзыв.
     *
     * @param review объект отзыва
     * @return созданный отзыв
     */
    public Review create(Review review) {
        validateUserAndFilm(review.getUserId(), review.getFilmId());
        Review createdReview = reviewDao.create(review);
        eventDao.addEvent(review.getUserId(), EventType.REVIEW, Operation.ADD, createdReview.getReviewId());
        return createdReview;
    }

    /**
     * Обновляет существующий отзыв.
     *
     * @param review объект отзыва
     * @return обновленный отзыв
     */
    public Review update(Review review) {
        Review existing = getById(review.getReviewId());

        // Проверяем, что userId и filmId не изменились
        if (!existing.getUserId().equals(review.getUserId())) {
            throw new ValidationException("Невозможно изменить пользователя отзыва");
        }
        if (!existing.getFilmId().equals(review.getFilmId())) {
            throw new ValidationException("Невозможно изменить фильм отзыва");
        }

        Review updatedReview = reviewDao.update(review);

        // Отправляем событие UPDATE только если изменился контент или оценка
        if (!existing.getContent().equals(review.getContent()) ||
                !existing.getIsPositive().equals(review.getIsPositive())) {
            eventDao.addEvent(review.getUserId(), EventType.REVIEW,
                    Operation.UPDATE, review.getReviewId());
        }
        return updatedReview;
    }

    /**
     * Удаляет отзыв по его идентификатору.
     *
     * @param id идентификатор отзыва
     */
    public void delete(Long id) {
        Review review = getById(id);
        reviewDao.delete(id);
        eventDao.addEvent(review.getUserId(), EventType.REVIEW, Operation.REMOVE, id);
    }

    /**
     * Получает отзыв по его идентификатору.
     *
     * @param id идентификатор отзыва
     * @return объект отзыва
     * @throws EntityNotFoundException если отзыв не найден
     */
    public Review getById(Long id) {
        return reviewDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Отзыв с id=" + id + " не найден"));
    }

    /**
     * Получает список отзывов для конкретного фильма или всех отзывов.
     *
     * @param filmId идентификатор фильма (если null, возвращает все отзывы)
     * @param count  максимальное число возвращаемых отзывов
     * @return список отзывов
     */
    public List<Review> getByFilmId(Long filmId, int count) {
        if (filmId != null) {
            filmStorage.getFilmById(filmId);
        }
        return filmId == null ?
                reviewDao.findAll(count) :
                reviewDao.findByFilmId(filmId, count);
    }

    /**
     * Добавляет лайк к отзыву от определенного пользователя.
     *
     * @param reviewId идентификатор отзыва
     * @param userId   идентификатор пользователя
     */
    public void addLike(Long reviewId, Long userId) {
        validateUser(userId);
        reviewDao.addLike(reviewId, userId);
    }

    /**
     * Добавляет дизлайк к отзыву от определенного пользователя.
     *
     * @param reviewId идентификатор отзыва
     * @param userId   идентификатор пользователя
     */
    public void addDislike(Long reviewId, Long userId) {
        validateUser(userId);
        reviewDao.addDislike(reviewId, userId);
    }

    /**
     * Удаляет лайк у отзыва от определенного пользователя.
     *
     * @param reviewId идентификатор отзыва
     * @param userId   идентификатор пользователя
     */
    public void removeLike(Long reviewId, Long userId) {
        validateUser(userId);
        reviewDao.removeLike(reviewId, userId);
    }

    /**
     * Удаляет дизлайк у отзыва от определенного пользователя.
     *
     * @param reviewId идентификатор отзыва
     * @param userId   идентификатор пользователя
     */
    public void removeDislike(Long reviewId, Long userId) {
        validateUser(userId);
        reviewDao.removeDislike(reviewId, userId);
    }

    /**
     * Проверяет существование пользователя и фильма перед операциями с отзывом.
     * Генерирует EntityNotFoundException если пользователь или фильм не найдены.
     *
     * @param userId идентификатор пользователя
     * @param filmId идентификатор фильма
     * @throws EntityNotFoundException если пользователь или фильм не существуют
     */
    private void validateUserAndFilm(Long userId, Long filmId) {
        userStorage.getUserById(userId);
        filmStorage.getFilmById(filmId);
    }

    /**
     * Валидирует наличие пользователя перед взаимодействием с отзывом.
     *
     * @param userId идентификатор пользователя
     */
    private void validateUser(Long userId) {
        userStorage.getUserById(userId);
    }
}