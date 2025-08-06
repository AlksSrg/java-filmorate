package ru.yandex.practicum.filmorate.storage.dao.like;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Реализация DAO для работы с лайками пользователей.
 * Обеспечивает хранение и получение лайков в базе данных.
 */

@AllArgsConstructor
@Component
public class LikeDaoImpl implements LikeDao {

    private final Logger log = LoggerFactory.getLogger(LikeDaoImpl.class);
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(Long userId, Long filmId) {
        try {
            jdbcTemplate.update("INSERT INTO likes (user_id, film_id) VALUES (?,?)", userId,
                    filmId);
            log.info("Добавлен лайк пользователя {} для фильма {}", userId, filmId);
        } catch (EntityNotFoundException e) {
            log.error("Ошибка при добавлении лайка пользователю {}: {}", userId, e.getMessage());
        }
    }

    @Override
    public void deleteLike(Long userId, Long filmId) {
        try {
            jdbcTemplate.update("DELETE FROM likes WHERE user_id = ? AND film_id = ?", userId,
                    filmId);
            log.info("Удален лайк пользователя {} для фильма {}", userId, filmId);
        } catch (EntityNotFoundException e) {
            log.error("Ошибка при удалении лайка пользователя {}: {}", userId, e.getMessage());
        }
    }

    @Override
    public int checkLikes(Long filmId) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM likes WHERE film_id=?",
                Integer.class, filmId);
        return count != null ? count : 0;
    }

    @Override
    public Map<Long, Set<Long>> getAllLikesMap() {
        String sql = "SELECT user_id, film_id FROM likes";
        return jdbcTemplate.query(sql, rs -> {
            Map<Long, Set<Long>> likesMap = new HashMap<>();
            while (rs.next()) {
                Long userId = rs.getLong("user_id");
                Long filmId = rs.getLong("film_id");
                likesMap.computeIfAbsent(userId, k -> new HashSet<>()).add(filmId);
            }
            return likesMap;
        });
    }

    @Override
    public Set<Long> getLikedFilms(Long userId) {
        String sql = "SELECT film_id FROM likes WHERE user_id = ?";
        return new HashSet<>(jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getLong("film_id"),
                userId
        ));
    }

    @Override
    public int getLikesCount(Long filmId) {
        String sql = "SELECT COUNT(*) FROM likes WHERE film_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, filmId);
        return count != null ? count : 0;
    }

    @Override
    public Map<Long, Integer> getLikesCountForAllFilms() {
        String sql = "SELECT film_id, COUNT(user_id) as like_count FROM likes GROUP BY film_id";
        return jdbcTemplate.query(sql, rs -> {
            Map<Long, Integer> result = new HashMap<>();
            while (rs.next()) {
                result.put(rs.getLong("film_id"), rs.getInt("like_count"));
            }
            return result;
        });
    }
}