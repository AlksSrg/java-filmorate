package ru.yandex.practicum.filmorate.storage.mapper;

import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.dto.ReviewDto;

/**
 * Маппер для преобразования между Review и ReviewDto.
 */

public class ReviewMapper {
    public static Review toReview(ReviewDto reviewDto) {
        Review review = new Review();
        review.setReviewId(reviewDto.getReviewId());
        review.setContent(reviewDto.getContent());
        review.setIsPositive(reviewDto.getIsPositive());
        review.setUserId(reviewDto.getUserId());
        review.setFilmId(reviewDto.getFilmId());
        return review;
    }

    public static ReviewDto toReviewDto(Review review) {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setReviewId(review.getReviewId());
        reviewDto.setContent(review.getContent());
        reviewDto.setIsPositive(review.getIsPositive());
        reviewDto.setUserId(review.getUserId());
        reviewDto.setFilmId(review.getFilmId());
        return reviewDto;
    }
}