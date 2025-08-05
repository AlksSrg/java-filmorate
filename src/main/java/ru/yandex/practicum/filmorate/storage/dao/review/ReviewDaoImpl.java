package ru.yandex.practicum.filmorate.storage.dao.review;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.mapper.FilmReviewMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ReviewDaoImpl implements ReviewDao {

    private final JdbcTemplate jdbcTemplate;

    public ReviewDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Review create(Review review) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("review_id");

        Map<String, Object> values = new HashMap<>();
        values.put("content", review.getContent());
        values.put("is_positive", review.getIsPositive());
        values.put("user_id", review.getUserId());
        values.put("film_id", review.getFilmId());
        values.put("useful", review.getUseful());

        Long reviewId = simpleJdbcInsert.executeAndReturnKey(values).longValue();
        review.setReviewId(reviewId);
        return review;
    }

    @Override
    public Review update(Review review) {
        String sql = "UPDATE reviews SET content = ?, is_positive = ?, useful = ? WHERE review_id = ?";
        jdbcTemplate.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getUseful(),
                review.getReviewId());
        return review;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Optional<Review> findById(Long id) {
        String sql = "SELECT * FROM reviews WHERE review_id = ?";
        List<Review> reviews = jdbcTemplate.query(sql, new FilmReviewMapper(), id);
        return reviews.stream().findFirst();
    }

    @Override
    public List<Review> findByFilmId(Long filmId, int count) {
        String sql = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
        return jdbcTemplate.query(sql, new FilmReviewMapper(), filmId, count);
    }

    @Override
    public List<Review> findAll(int count) {
        String sql = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
        return jdbcTemplate.query(sql, new FilmReviewMapper(), count);
    }

    @Override
    public void addLike(Long reviewId, Long userId) {

        String checkSql = "SELECT COUNT(*) FROM review_likes WHERE review_id = ? AND user_id = ?";
        int count = jdbcTemplate.queryForObject(checkSql, Integer.class, reviewId, userId);

        if (count > 0) {
            String updateSql = "UPDATE review_likes SET is_like = true WHERE review_id = ? AND user_id = ?";
            jdbcTemplate.update(updateSql, reviewId, userId);
        } else {
            String insertSql = "INSERT INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, true)";
            jdbcTemplate.update(insertSql, reviewId, userId);
        }
        updateUseful(reviewId);
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {

        String checkSql = "SELECT COUNT(*) FROM review_likes WHERE review_id = ? AND user_id = ?";
        int count = jdbcTemplate.queryForObject(checkSql, Integer.class, reviewId, userId);

        if (count > 0) {
            String updateSql = "UPDATE review_likes SET is_like = false WHERE review_id = ? AND user_id = ?";
            jdbcTemplate.update(updateSql, reviewId, userId);
        } else {
            String insertSql = "INSERT INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, false)";
            jdbcTemplate.update(insertSql, reviewId, userId);
        }
        updateUseful(reviewId);
    }

    @Override
    public void removeLike(Long reviewId, Long userId) {
        String sql = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, reviewId, userId);
        updateUseful(reviewId);
    }

    @Override
    public void removeDislike(Long reviewId, Long userId) {
        String sql = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, reviewId, userId);
        updateUseful(reviewId);
    }

    private void updateUseful(Long reviewId) {
        String sql = "UPDATE reviews SET useful = " +
                "(SELECT COUNT(*) FROM review_likes WHERE review_id = ? AND is_like = true) - "
                +
                "(SELECT COUNT(*) FROM review_likes WHERE review_id = ? AND is_like = false) "
                +
                "WHERE review_id = ?";
        jdbcTemplate.update(sql, reviewId, reviewId, reviewId);
    }
}