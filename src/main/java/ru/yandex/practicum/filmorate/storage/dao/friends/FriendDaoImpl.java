package ru.yandex.practicum.filmorate.storage.dao.friends;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;

import java.util.List;

@AllArgsConstructor
@Component
public class FriendDaoImpl implements FriendDao {

    private final JdbcTemplate jdbcTemplate;

    private boolean existsUser(Long userId) {
        return jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM users WHERE user_id = ?)", Boolean.class,
                userId
        );
    }

    @Override
    public void addFriends(Long userId, Long idFriend, boolean status) {
        if (!existsUser(userId) || !existsUser(idFriend)) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
        jdbcTemplate.update(
                "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, ?)",
                userId, idFriend, status
        );
    }

    @Override
    public void deleteFriends(Long userId, Long idFriend) {
        if (!existsUser(userId)) {
            throw new EntityNotFoundException("Пользователь с id " + userId + " не найден");
        }
        jdbcTemplate.update(
                "DELETE FROM friends WHERE user_id = ? AND friend_id = ?",
                userId, idFriend
        );
        jdbcTemplate.update(
                "UPDATE friends SET status = FALSE WHERE user_id = ? AND friend_id = ?",
                idFriend, userId
        );
    }

    @Override
    public boolean statusFriend(Long userId, Long friendId) {
        try {
            jdbcTemplate.queryForObject(
                    "SELECT EXISTS(SELECT 1 FROM friends WHERE user_id = ? AND friend_id = ?)", Boolean.class,
                    userId, friendId
            );
            return true;
        } catch (EmptyResultDataAccessException ignored) {
            return false;
        }
    }

    @Override
    public List<Long> getFriends(Long userId) {
        return jdbcTemplate.queryForList(
                "SELECT friend_id FROM friends WHERE user_id = ?",
                Long.class,
                userId
        );
    }
}