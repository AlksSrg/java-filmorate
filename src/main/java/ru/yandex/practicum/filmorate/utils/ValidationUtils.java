package ru.yandex.practicum.filmorate.utils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.groups.Default;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.util.Set;

// Утилитарный класс для общих проверок валидации
public class ValidationUtils {

    private static final Validator validator;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    //Основная проверка объекта на предмет наличия ошибок валидации.
    public static void validate(Object object) throws ValidationException {
        Set<ConstraintViolation<Object>> violations = validator.validate(object, Default.class);
        if (!violations.isEmpty()) {
            throw new ValidationException(violations.iterator().next().getMessage());
        }
    }

    //Проверка валидности адреса электронной почты.
    public static boolean isValidEmail(String email) {
        return email != null && !email.trim().isEmpty() &&
                email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    }

    //Проверка обязательного заполнения логина пользователя.
    public static boolean isValidLogin(String login) {
        return login != null && !login.trim().isEmpty();
    }

    //Проверка допустимости даты рождения пользователя.
    public static boolean isValidBirthday(LocalDate birthday) {
        return birthday != null && !birthday.isAfter(LocalDate.now());
    }

    //Проверка правильности установки имени пользователя (необязательно, заполняется автоматически,если пустое).
    public static boolean isValidUsername(String name) {
        return true; // Мы разрешаем пропуск имени, оно заменится на значение логина, если пустое
    }

    //Валидная продолжительность фильма (для Film).
    public static boolean isValidDuration(Integer duration) {
        return duration > 0;
    }

    //Валидный релиз фильма (для Film).
    public static boolean isValidReleaseDate(LocalDate date) {
        return date != null && !date.isBefore(LocalDate.of(1895, 12, 28));
    }

    //Валидность названия фильма (для Film).
    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty();
    }
}