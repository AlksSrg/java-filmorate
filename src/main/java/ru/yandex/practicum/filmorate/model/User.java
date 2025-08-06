package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}