package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

/**
 * Контроллер для работы с отзывами на фильмы.
 * Обеспечивает REST API для управления отзывами и их оценками.
 * Поддерживает операции:
 * - CRUD операции с отзывами
 * - Управление лайками и дизлайками отзывов
 * - Получение отзывов по различным критериям
 * <p>
 * Все методы работают с сущностью {@link Review} и используют {@link ReviewService} для бизнес-логики.
 * Для преобразования DTO используется {@link ReviewMapper}.
 */

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    /**
     * Сервис отзывов для взаимодействия с бизнес-логикой.
     */
    private final ReviewService reviewService;

    /**
     * Маппер для преобразования DTO объектов в модели и обратно.
     */
    private final ReviewMapper reviewMapper;

    /**
     * Конструктор контроллера с внедрением зависимостей сервиса и маппера.
     *
     * @param reviewService сервис отзывов
     * @param reviewMapper  маппер отзывов
     */
    @Autowired
    public ReviewController(ReviewService reviewService, ReviewMapper reviewMapper) {
        this.reviewService = reviewService;
        this.reviewMapper = reviewMapper;
    }

    /**
     * Создает новый отзыв.
     *
     * @param reviewDto объект отзыва в виде DTO
     * @return созданный отзыв
     */
    @PostMapping
    public Review create(@Valid @RequestBody ReviewDto reviewDto) {
        return reviewService.create(reviewMapper.toReview(reviewDto));
    }

    /**
     * Обновляет существующий отзыв.
     * Проверяет наличие отзыва и сохраняет оригинальные userId и filmId из базы данных.
     *
     * @param reviewDto объект отзыва в виде DTO с обновленными данными
     * @return обновленный отзыв
     * @throws ValidationException если ID отзыва отсутствует или равен null
     */
    @PutMapping
    public Review update(@Valid @RequestBody ReviewDto reviewDto) {
        if (reviewDto.getReviewId() == null) {
            throw new ValidationException("ID отзыва не может быть null");
        }

        Review existingReview = reviewService.getById(reviewDto.getReviewId());

        reviewDto.setUserId(existingReview.getUserId());
        reviewDto.setFilmId(existingReview.getFilmId());

        return reviewService.update(reviewMapper.toReview(reviewDto));
    }

    /**
     * Удаляет отзыв по указанному ID.
     *
     * @param id уникальный идентификатор отзыва
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        reviewService.delete(id);
    }

    /**
     * Получает отзыв по уникальному идентификатору.
     *
     * @param id уникальный идентификатор отзыва
     * @return найденный отзыв
     */
    @GetMapping("/{id}")
    public Review getById(@PathVariable Long id) {
        return reviewService.getById(id);
    }

    /**
     * Возвращает список отзывов фильма либо всех отзывов, если фильм не указан.
     *
     * @param filmId идентификатор фильма (необязательно)
     * @param count  количество возвращаемых отзывов (по умолчанию 10)
     * @return список отзывов
     */
    @GetMapping
    public List<Review> getByFilmId(
            @RequestParam(required = false) Long filmId,
            @RequestParam(defaultValue = "10") int count) {
        return reviewService.getByFilmId(filmId, count);
    }

    /**
     * Добавляет лайк к отзыву от указанного пользователя.
     *
     * @param id     идентификатор отзыва
     * @param userId идентификатор пользователя
     */
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.addLike(id, userId);
    }

    /**
     * Добавляет дизлайк к отзыву от указанного пользователя.
     *
     * @param id     идентификатор отзыва
     * @param userId идентификатор пользователя
     */
    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.addDislike(id, userId);
    }

    /**
     * Убирает лайк у отзыва от указанного пользователя.
     *
     * @param id     идентификатор отзыва
     * @param userId идентификатор пользователя
     */
    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.removeLike(id, userId);
    }

    /**
     * Убирает дизлайк у отзыва от указанного пользователя.
     *
     * @param id     идентификатор отзыва
     * @param userId идентификатор пользователя
     */
    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.removeDislike(id, userId);
    }
}