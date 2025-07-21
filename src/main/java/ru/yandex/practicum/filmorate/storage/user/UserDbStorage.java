package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.UserMapper;

import java.sql.Date;
import java.util.Collection;

@Slf4j
@Component("UserDbStorage")
@RequiredArgsConstructor
@Primary
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User create(User user) {
        log.debug("Добавление нового пользователя.");
        try {
            if (user.getName() == null || user.getName().trim().isEmpty()) {
                user.setName(user.getLogin());
            }
            jdbcTemplate.update(
                    "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                    user.getEmail(), user.getLogin(), user.getName(), Date.valueOf(user.getBirthday())
            );
            return jdbcTemplate.queryForObject(
                    "SELECT user_id, email, login, name, birthday FROM users WHERE email = ?",
                    new UserMapper(), user.getEmail()
            );
        } catch (ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    @Override
    public User updateUser(User user) {
        Long userId = user.getId();

        // Проверяем, существует ли пользователь с данным ID
        int rowsAffected = jdbcTemplate.update(
                "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?",
                user.getEmail(), user.getLogin(), user.getName(), Date.valueOf(user.getBirthday()), userId
        );

        if (rowsAffected == 0) {
            log.warn("Пользователь с id {} не найден", userId);
            throw new EntityNotFoundException("Пользователь с id " + userId + " не найден");
        }

        log.debug("Пользователь обновлен");
        return user;
    }

    @Override
    public Collection<User> getUser() {
        return jdbcTemplate.query("SELECT * FROM users", new UserMapper());
    }

    @Override
    public User getUserById(Long id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM users WHERE user_id = ?", new UserMapper(), id);
        } catch (EmptyResultDataAccessException exception) {
            throw new EntityNotFoundException(String.format("Пользователя с id %s не существует", id));
        }
    }
}