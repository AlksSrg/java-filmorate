package ru.yandex.practicum.filmorate.storage.dao.review;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewDao;

/**
 * Реализация DAO для работы с отзывами в базе данных.
 * Обеспечивает хранение, извлечение и модификацию данных об отзывах,
 * а также управление лайками и дизлайками отзывов.
 */
@Slf4j
@Repository
public class ReviewDaoImpl implements ReviewDao {
    private static final String REVIEWS_TABLE = "reviews";
    private static final String REVIEW_LIKES_TABLE = "review_likes";
    private static final String REVIEW_ID_COLUMN = "review_id";
    private static final String USER_ID_COLUMN = "user_id";
    private static final String IS_LIKE_COLUMN = "is_like";

    private final JdbcTemplate jdbcTemplate;

    public ReviewDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Создает новый отзыв в базе данных.
     *
     * @param review объект отзыва
     * @return созданный отзыв с присвоенным идентификатором
     */
    @Override
    public Review create(Review review) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
            .withTableName(REVIEWS_TABLE)
            .usingGeneratedKeyColumns(REVIEW_ID_COLUMN);

        Map<String, Object> values = new HashMap<>();
        values.put("content", review.getContent());
        values.put("is_positive", review.getIsPositive());
        values.put(USER_ID_COLUMN, review.getUserId());
        values.put("film_id", review.getFilmId());
        values.put("useful", review.getUseful());

        Long reviewId = simpleJdbcInsert.executeAndReturnKey(values).longValue();
        review.setReviewId(reviewId);
        log.debug("Создан новый отзыв с ID: {}", reviewId);
        return review;
    }

    /**
     * Обновляет существующий отзыв в базе данных.
     *
     * @param review объект отзыва с обновленными данными
     * @return обновленный отзыв
     */
    @Override
    public Review update(Review review) {
        String sql = "UPDATE reviews SET content = ?, is_positive = ?, useful = ? WHERE review_id = ?";
        jdbcTemplate.update(sql,
            review.getContent(),
            review.getIsPositive(),
            review.getUseful(),
            review.getReviewId());
        log.debug("Обновлен отзыв с ID: {}", review.getReviewId());
        return review;
    }

    /**
     * Удаляет отзыв из базы данных по идентификатору.
     *
     * @param id идентификатор отзыва
     */
    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        jdbcTemplate.update(sql, id);
        log.debug("Удален отзыв с ID: {}", id);
    }

    /**
     * Находит отзыв по идентификатору.
     *
     * @param id идентификатор отзыва
     * @return Optional с найденным отзывом или пустой, если не найден
     */
    @Override
    public Optional<Review> findById(Long id) {
        String sql = "SELECT * FROM reviews WHERE review_id = ?";
        List<Review> reviews = jdbcTemplate.query(sql, this::mapRowToReview, id);
        return reviews.stream().findFirst();
    }

    /**
     * Находит все отзывы для указанного фильма с ограничением по количеству.
     *
     * @param filmId идентификатор фильма
     * @param count  максимальное количество возвращаемых отзывов
     * @return список отзывов, отсортированных по полезности
     */
    @Override
    public List<Review> findByFilmId(Long filmId, int count) {
        String sql = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
        return jdbcTemplate.query(sql, this::mapRowToReview, filmId, count);
    }

    /**
     * Находит все отзывы с ограничением по количеству.
     *
     * @param count максимальное количество возвращаемых отзывов
     * @return список отзывов, отсортированных по полезности
     */
    @Override
    public List<Review> findAll(int count) {
        String sql = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
        return jdbcTemplate.query(sql, this::mapRowToReview, count);
    }

    /**
     * Добавляет лайк отзыву от пользователя.
     *
     * @param reviewId идентификатор отзыва
     * @param userId   идентификатор пользователя
     */
    @Override
    public void addLike(Long reviewId, Long userId) {
        String sql = String.format(
            "MERGE INTO %s (%s, %s, %s) KEY (%s, %s) VALUES (?, ?, true)",
            REVIEW_LIKES_TABLE, REVIEW_ID_COLUMN, USER_ID_COLUMN, IS_LIKE_COLUMN,
            REVIEW_ID_COLUMN, USER_ID_COLUMN);
        jdbcTemplate.update(sql, reviewId, userId);
        updateUseful(reviewId);
        log.debug("Добавлен лайк отзыву ID: {} от пользователя ID: {}", reviewId, userId);
    }

    /**
     * Добавляет дизлайк отзыву от пользователя.
     *
     * @param reviewId идентификатор отзыва
     * @param userId   идентификатор пользователя
     */
    @Override
    public void addDislike(Long reviewId, Long userId) {
        String sql = String.format(
            "MERGE INTO %s (%s, %s, %s) KEY (%s, %s) VALUES (?, ?, false)",
            REVIEW_LIKES_TABLE, REVIEW_ID_COLUMN, USER_ID_COLUMN, IS_LIKE_COLUMN,
            REVIEW_ID_COLUMN, USER_ID_COLUMN);
        jdbcTemplate.update(sql, reviewId, userId);
        updateUseful(reviewId);
        log.debug("Добавлен дизлайк отзыву ID: {} от пользователя ID: {}", reviewId, userId);
    }

    /**
     * Удаляет лайк отзыва от пользователя.
     *
     * @param reviewId идентификатор отзыва
     * @param userId   идентификатор пользователя
     */
    @Override
    public void removeLike(Long reviewId, Long userId) {
        String sql = String.format(
            "DELETE FROM %s WHERE %s = ? AND %s = ? AND %s = true",
            REVIEW_LIKES_TABLE, REVIEW_ID_COLUMN, USER_ID_COLUMN, IS_LIKE_COLUMN);
        jdbcTemplate.update(sql, reviewId, userId);
        updateUseful(reviewId);
        log.debug("Удален лайк отзыву ID: {} от пользователя ID: {}", reviewId, userId);
    }

    /**
     * Удаляет дизлайк отзыва от пользователя.
     *
     * @param reviewId идентификатор отзыва
     * @param userId   идентификатор пользователя
     */
    @Override
    public void removeDislike(Long reviewId, Long userId) {
        String sql = String.format(
            "DELETE FROM %s WHERE %s = ? AND %s = ? AND %s = false",
            REVIEW_LIKES_TABLE, REVIEW_ID_COLUMN, USER_ID_COLUMN, IS_LIKE_COLUMN);
        jdbcTemplate.update(sql, reviewId, userId);
        updateUseful(reviewId);
        log.debug("Удален дизлайк отзыву ID: {} от пользователя ID: {}", reviewId, userId);
    }

    /**
     * Проверяет, поставил ли пользователь лайк отзыву.
     *
     * @param reviewId идентификатор отзыва
     * @param userId   идентификатор пользователя
     * @return true, если пользователь поставил лайк, иначе false
     */
    @Override
    public boolean hasLike(Long reviewId, Long userId) {
        String sql = String.format(
            "SELECT COUNT(*) FROM %s WHERE %s = ? AND %s = ? AND %s = true",
            REVIEW_LIKES_TABLE, REVIEW_ID_COLUMN, USER_ID_COLUMN, IS_LIKE_COLUMN);
        return jdbcTemplate.queryForObject(sql, Integer.class, reviewId, userId) > 0;
    }

    /**
     * Проверяет, поставил ли пользователь дизлайк отзыву.
     *
     * @param reviewId идентификатор отзыва
     * @param userId   идентификатор пользователя
     * @return true, если пользователь поставил дизлайк, иначе false
     */
    @Override
    public boolean hasDislike(Long reviewId, Long userId) {
        String sql = String.format(
            "SELECT COUNT(*) FROM %s WHERE %s = ? AND %s = ? AND %s = false",
            REVIEW_LIKES_TABLE, REVIEW_ID_COLUMN, USER_ID_COLUMN, IS_LIKE_COLUMN);
        return jdbcTemplate.queryForObject(sql, Integer.class, reviewId, userId) > 0;
    }

    /**
     * Обновляет рейтинг полезности отзыва.
     *
     * @param reviewId идентификатор отзыва
     */
    private void updateUseful(Long reviewId) {
        String sql = "UPDATE reviews SET useful = " +
                     "(SELECT COALESCE(SUM(CASE WHEN is_like = true THEN 1 ELSE -1 END), 0) " +
                     "FROM review_likes WHERE review_id = ?) " +
                     "WHERE review_id = ?";
        jdbcTemplate.update(sql, reviewId, reviewId);
    }

    /**
     * Преобразует строку результата SQL-запроса в объект Review.
     *
     * @param rs     результат SQL-запроса
     * @param rowNum номер строки
     * @return объект Review
     * @throws SQLException в случае ошибки доступа к данным
     */
    private Review mapRowToReview(ResultSet rs, int rowNum) throws SQLException {
        Review review = new Review();
        review.setReviewId(rs.getLong("review_id"));
        review.setContent(rs.getString("content"));
        review.setIsPositive(rs.getBoolean("is_positive"));
        review.setUserId(rs.getLong("user_id"));
        review.setFilmId(rs.getLong("film_id"));
        review.setUseful(rs.getInt("useful"));
        return review;
    }
}