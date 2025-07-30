package ru.yandex.practicum.filmorate.dal.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.ReviewLike;

@Component
public class ReviewLikeRowMapper implements RowMapper<ReviewLike> {

    @Override
    public ReviewLike mapRow(ResultSet rs, int rowNum) throws SQLException {
        ReviewLike reviewLike = new ReviewLike();
        reviewLike.setReviewId(rs.getLong("review_id"));
        reviewLike.setUserId(rs.getLong("user_id"));
        reviewLike.setIsLike(rs.getBoolean("is_like"));
        return reviewLike;
    }
}