package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

/**
 * Контроллер для управления отзывами на фильмы.
 * Обрабатывает HTTP-запросы для создания, обновления, удаления и получения отзывов,
 * а также для управления лайками и дизлайками отзывов.
 */
@Slf4j
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
        log.info("Создание нового отзыва: {}", reviewDto);
        return reviewService.create(reviewMapper.toReview(reviewDto));
    }

    /**
     * Обновляет существующий отзыв.
     *
     * @param reviewDto DTO с обновленными данными отзыва
     * @return обновленный отзыв
     * @throws EntityNotFoundException если отзыв не найден
     */
    @PutMapping
    public Review update(@Valid @RequestBody ReviewDto reviewDto) {
        log.info("Обновление отзыва с ID: {}", reviewDto);
        Review review = reviewMapper.toReview(reviewDto);
        return reviewService.update(review);
    }

    /**
     * Удаляет отзыв по идентификатору.
     *
     * @param id идентификатор отзыва
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Удаление отзыва с ID: {}", id);
        reviewService.delete(id);
    }

    /**
     * Получает отзыв по идентификатору.
     *
     * @param id идентификатор отзыва
     * @return найденный отзыв
     * @throws EntityNotFoundException если отзыв не найден
     */
    @GetMapping("/{id}")
    public Review getById(@PathVariable Long id) {
        log.info("Получение отзыва с ID: {}", id);
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
        log.info("Получение отзывов для фильма ID: {}, количество: {}", filmId, count);
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
        log.info("Добавление лайка отзыву ID: {} от пользователя ID: {}", id, userId);
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
        log.info("Добавление дизлайка отзыву ID: {} от пользователя ID: {}", id, userId);
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
        log.info("Удаление лайка отзыву ID: {} от пользователя ID: {}", id, userId);
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
        log.info("Удаление дизлайка отзыву ID: {} от пользователя ID: {}", id, userId);
        reviewService.removeDislike(id, userId);
    }
}