package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.model.Review;

/**
 * Маппер для преобразования между DTO и моделью отзыва.
 * Обеспечивает конвертацию объектов ReviewDto в Review и обратно.
 */
@Component
public class ReviewMapper {
    /**
     * Преобразует ReviewDto в Review.
     *
     * @param reviewDto DTO отзыва
     * @return объект модели Review
     */
    public Review toReview(ReviewDto reviewDto) {
        Review review = new Review();
        review.setContent(reviewDto.getContent());
        review.setIsPositive(reviewDto.getIsPositive());
        review.setUserId(reviewDto.getUserId());
        review.setFilmId(reviewDto.getFilmId());
        return review;
    }

    /**
     * Преобразует Review в ReviewDto.
     *
     * @param review объект модели Review
     * @return DTO отзыва
     */
    public ReviewDto toReviewDto(Review review) {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setContent(review.getContent());
        reviewDto.setIsPositive(review.getIsPositive());
        reviewDto.setUserId(review.getUserId());
        reviewDto.setFilmId(review.getFilmId());
        return reviewDto;
    }
}