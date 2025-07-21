package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class User {

    /**
     * Поле с id пользователя
     */
    Long id;

    /**
     * Поле с почтой пользователя
     */
    @Email(message = "Электронная почта должна соответствовать стандартному формату.")
    @NotBlank(message = "Адрес электронной почты не может быть пустым.")
    @NotNull
    String email;

    /**
     * Поле с логином пользователя
     */
    @NotBlank(message = "Логин пользователя должен быть заполнен.")
    @NotEmpty
    String login;

    /**
     * Поле с именем пользователя
     */
    String name;

    /**
     * Поле с датой рождения пользователя
     */
    @PastOrPresent(message = "Дата рождения не может быть в будущем.")
    @NotNull
    LocalDate birthday;

    /**
     * Конструктор создание нового объекта пользователя.
     */
    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}