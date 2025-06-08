package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    Long id;

    @Email(message = "Электронная почта должна соответствовать стандартному формату.")
    @NotBlank(message = "Адрес электронной почты не может быть пустым.")
    String email;

    @NotBlank(message = "Логин пользователя должен быть заполнен.")
    String login;

    String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем.")
    LocalDate birthday;
}