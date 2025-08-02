package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

/**
 * Контроллер для управления отзывами на фильмы.
 * Обрабатывает HTTP-запросы для создания, обновления, удаления и получения отзывов,
 * а также для управления лайками и дизлайками отзывов.
 */
@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    @Autowired
    public ReviewController(ReviewService reviewService, ReviewMapper reviewMapper) {
        this.reviewService = reviewService;
        this.reviewMapper = reviewMapper;
    }

    /**
     * Создает новый отзыв.
     *
     * @param reviewDto DTO с данными отзыва
     * @return созданный отзыв
     */
    @PostMapping
    public Review create(@Valid @RequestBody ReviewDto reviewDto) {
        return reviewService.create(reviewMapper.toReview(reviewDto));
    }

    @PutMapping
    public Review update(@Valid @RequestBody ReviewDto reviewDto) {
        Review review = reviewMapper.toReview(reviewDto);
        if (review.getReviewId() == null) {
            throw new ValidationException("ID отзыва не может быть null");
        }
        return reviewService.update(review);
    }

    /**
     * Удаляет отзыв по идентификатору.
     *
     * @param id идентификатор отзыва
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        reviewService.delete(id);
    }

    @GetMapping("/{id}")
    public Review getById(@PathVariable Long id) {
        return reviewService.getById(id);
    }

    /**
     * Получает список отзывов для указанного фильма или все отзывы, если фильм не указан.
     *
     * @param filmId идентификатор фильма (опционально)
     * @param count  количество возвращаемых отзывов (по умолчанию 10)
     * @return список отзывов, отсортированных по полезности
     */
    @GetMapping
    public List<Review> getByFilmId(
        @RequestParam(required = false) Long filmId,
        @RequestParam(defaultValue = "10") int count) {
        return reviewService.getByFilmId(filmId, count);
    }

    /**
     * Добавляет лайк отзыву от пользователя.
     *
     * @param id     идентификатор отзыва
     * @param userId идентификатор пользователя
     */
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.addLike(id, userId);
    }

    /**
     * Добавляет дизлайк отзыву от пользователя.
     *
     * @param id     идентификатор отзыва
     * @param userId идентификатор пользователя
     */
    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.addDislike(id, userId);
    }

    /**
     * Удаляет лайк/дизлайк отзыва от пользователя.
     *
     * @param id     идентификатор отзыва
     * @param userId идентификатор пользователя
     */
    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.removeLike(id, userId);
    }

    /**
     * Удаляет дизлайк отзыва от пользователя.
     *
     * @param id     идентификатор отзыва
     * @param userId идентификатор пользователя
     */
    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.removeDislike(id, userId);
    }
}