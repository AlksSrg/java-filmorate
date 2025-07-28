package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class User {

    /**
     * Уникальный идентификатор пользователя.
     */
    private Long id;

    /**
     * Электронная почта пользователя, должна быть валидной и обязательной.
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
     * Имя пользователя (может быть пустым).
     */
    private String name;

    /**
     * Дата рождения пользователя, обязательная и не может быть в будущем.
     */
    @PastOrPresent(message = "Дата рождения не может быть в будущем.")
    @NotNull
    private LocalDate birthday;

    /**
     * Конструктор для создания экземпляра пользователя с указанными параметрами.
     *
     * @param email    электронная почта пользователя
     * @param login    логин пользователя
     * @param name     имя пользователя
     * @param birthday дата рождения пользователя
     */
    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}