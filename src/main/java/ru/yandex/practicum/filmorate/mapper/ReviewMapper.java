package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.model.Review;

/**
 * Маппер для преобразования между Review и ReviewDto.
 */
@Component
public class ReviewMapper {
    public Review toReview(ReviewDto reviewDto) {
        Review review = new Review();
        review.setReviewId(reviewDto.getReviewId());  // Установите reviewId
        review.setContent(reviewDto.getContent());
        review.setIsPositive(reviewDto.getIsPositive());
        review.setUserId(reviewDto.getUserId());
        review.setFilmId(reviewDto.getFilmId());
        return review;
    }

    public ReviewDto toReviewDto(Review review) {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setReviewId(review.getReviewId());  // Добавьте это
        reviewDto.setContent(review.getContent());
        reviewDto.setIsPositive(review.getIsPositive());
        reviewDto.setUserId(review.getUserId());
        reviewDto.setFilmId(review.getFilmId());
        return reviewDto;
    }
}