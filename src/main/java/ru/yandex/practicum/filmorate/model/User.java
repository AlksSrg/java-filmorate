package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Модель пользователя системы.
 * Содержит основную информацию о пользователе:
 * идентификатор, email, логин, имя и дату рождения.
 * Поддерживает валидацию всех обязательных полей.
 */

@Data
@NoArgsConstructor
public class User {

    /**
     * Уникальный идентификатор пользователя.
     */
    private Long id;

    /**
     * Электронная почта. Обязательное поле, должно быть валидным email.
     */
    @Email(message = "Электронная почта должна соответствовать стандартному формату.")
    @NotBlank(message = "Адрес электронной почты не может быть пустым.")
    @NotNull
    private String email;

    /**
     * Логин пользователя, обязательный для заполнения.
     */
    @NotBlank(message = "Логин пользователя должен быть заполнен.")
    @NotEmpty
    private String login;

    /**
     * Имя пользователя. Если не указано, используется логин.
     */
    private String name;

    /**
     * Дата рождения пользователя, обязательная и не может быть в будущем.
     */
    @PastOrPresent(message = "Дата рождения не может быть в будущем.")
    @NotNull
    private LocalDate birthday;

    /**
     * Конструктор для создания пользователя с указанными параметрами.
     *
     * @param email    электронная почта (обязательное поле, валидный email)
     * @param login    логин пользователя (обязательное поле, не может содержать пробелы)
     * @param name     имя пользователя (если не указано, используется логин)
     * @param birthday дата рождения (не может быть в будущем)
     */
    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}