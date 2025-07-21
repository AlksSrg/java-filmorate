package ru.yandex.practicum.filmorate.utils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.groups.Default;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.genre.GenreDao;
import ru.yandex.practicum.filmorate.storage.dao.mpa.MpaDao;

import java.time.LocalDate;
import java.util.Set;

// Утилитарный класс для общих проверок валидации
public class ValidationUtils {

    private static final Validator validator;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // Общая проверка объекта на наличие ошибок валидации.
    public static void validate(Object object) throws ValidationException {
        Set<ConstraintViolation<Object>> violations = validator.validate(object, Default.class);
        if (!violations.isEmpty()) {
            throw new ValidationException(violations.iterator().next().getMessage());
        }
    }

    // Проверка валидности адреса электронной почты.
    public static boolean isValidEmail(String email) {
        return email != null && !email.trim().isEmpty() &&
                email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    }

    // Проверка обязательного заполнения логина пользователя.
    public static boolean isValidLogin(String login) {
        return login != null && !login.trim().isEmpty();
    }

    // Проверка допустимости даты рождения пользователя.
    public static boolean isValidBirthday(LocalDate birthday) {
        return birthday != null && !birthday.isAfter(LocalDate.now());
    }

    // Проверка правильности установки имени пользователя (необязательно, заполняется автоматически, если пустое).
    public static boolean isValidUsername(String name) {
        return true; // Мы разрешаем пропуск имени, оно заменится на значение логина, если пустое
    }

    // Валидная продолжительность фильма (для Film).
    public static boolean isValidDuration(Integer duration) {
        return duration > 0;
    }

    // Валидный релиз фильма (для Film).
    public static boolean isValidReleaseDate(LocalDate date) {
        return date != null && !date.isBefore(LocalDate.of(1895, 12, 28));
    }

    // Валидность названия фильма (для Film).
    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty();
    }

    // Новый метод для полной проверки фильма
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

        // Проверка рейтинга
        if (mpaDao.getMpaById(film.getMpa().getId()) == null) {
            throw new ValidationException("Рейтинг с ID " + film.getMpa().getId() + " не существует");
        }

        // Проверка жанров
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                if (genreDao.getGenreById(genre.getId()) == null) {
                    throw new ValidationException("Жанр с ID " + genre.getId() + " не существует");
                }
            }
        }
    }
}