package ru.yandex.practicum.filmorate.utils;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.genre.GenreDao;
import ru.yandex.practicum.filmorate.storage.dao.mpa.MpaDao;

import java.time.LocalDate;

/**
 * Утилита для общих проверок валидации.
 */
public class ValidationUtils {

    private static final Validator validator;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * Полностью проверяет объект {@link User} на валидность.
     *
     * @param user объект пользователя
     * @throws ValidationException если выявлены ошибки валидации
     */
    public static void validateUser(User user) throws ValidationException {

        // Проверка Email
        if (user.getEmail() == null || user.getEmail().trim().isEmpty() ||
                !user.getEmail().matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
            throw new ValidationException("Некорректный формат Email.");
        }

        // Проверка даты рождения
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Неправильная дата рождения.");
        }

        // Проверка логина
        if (user.getLogin() == null || user.getLogin().trim().isEmpty()) {
            throw new ValidationException("Логин не может быть пустым.");
        }
    }

    /**
     * Полностью проверяет объект {@link Film} на валидность.
     *
     * @param film     объект фильма
     * @param mpaDao   DAO для работы с рейтингами MPA
     * @param genreDao DAO для работы с жанрами
     * @throws ValidationException если выявлены ошибки валидации
     */
    public static void validateFilm(Film film, MpaDao mpaDao, GenreDao genreDao) throws ValidationException {
        // Проверка названия фильма
        if (film.getName() == null || film.getName().trim().isEmpty()) {
            throw new ValidationException("Название фильма обязательно");
        }

        // Проверка даты релиза
        LocalDate releaseDate = film.getReleaseDate();
        if (releaseDate == null || releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата выпуска фильма не должна быть раньше 28 декабря 1895 года");
        }

        // Проверка продолжительности фильма
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }

        // Проверка существования рейтинга
        if (mpaDao.getMpaById(film.getMpa().getId()) == null) {
            throw new ValidationException("Рейтинг с ID " + film.getMpa().getId() + " не существует");
        }

        // Проверка жанров фильма
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                if (genreDao.getGenreById(genre.getId()) == null) {
                    throw new ValidationException("Жанр с ID " + genre.getId() + " не существует");
                }
            }
        }
    }
}