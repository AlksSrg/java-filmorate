package ru.yandex.practicum.filmorate.storage.dao.user;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.UserMapper;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Date;
import java.util.Collection;

@AllArgsConstructor
@Component
public class UserDaoImpl implements UserStorage {

    private final Logger log = LoggerFactory.getLogger(UserDaoImpl.class);
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User addUser(User user) {
        jdbcTemplate.update(
                "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                user.getEmail(), user.getLogin(), user.getName(), Date.valueOf(user.getBirthday())
        );

        log.info("Создан новый пользователь с email {}", user.getEmail());
        return jdbcTemplate.queryForObject(
                "SELECT user_id, email, login, name, birthday FROM users WHERE email=?",
                new UserMapper(), user.getEmail()
        );
    }

    @Override
    public User updateUser(User user) {
        jdbcTemplate.update(
                "UPDATE users SET email=?, login=?, name=?, birthday=? WHERE user_id=?",
                user.getEmail(), user.getLogin(), user.getName(), Date.valueOf(user.getBirthday()), user.getId()
        );

        log.info("Обновлён пользователь с id {}", user.getId());
        return user;
    }

    @Override
    public Collection<User> getUsers() {
        return jdbcTemplate.query("SELECT * FROM users", new UserMapper());
    }

    @Override
    public User getUserById(Long id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM users WHERE user_id = ?", new UserMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("Пользователь с id " + id + " не найден");
        }
    }

    @Override
    public void deleteById(long id) {
        if (jdbcTemplate.update("DELETE FROM users WHERE user_id = ?", id) == 0) {
            throw new EntityNotFoundException(String.format("Пользователя с id %s и так не существует", id));
        }
    }
}