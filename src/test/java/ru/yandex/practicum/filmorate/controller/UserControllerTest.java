package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserControllerTest {

    private UserController controller;
    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        controller = new UserController(new UserService(new InMemoryUserStorage() {
        }));
    }

    //Тестирование успешного добавления пользователя с корректными данными.
    @Test
    void shouldAddUserSuccessfully() {
        User user = new User();
        user.setEmail("john.doe@example.com");
        user.setLogin("JohnDoe");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = controller.addUser(user);

        assertThat(createdUser.getId()).isGreaterThan(0);
        assertThat(createdUser.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(createdUser.getLogin()).isEqualTo("JohnDoe");
        assertThat(createdUser.getBirthday()).isEqualTo(LocalDate.of(1990, 1, 1));
    }

    //Тестирование добавления пользователя с пустым адресом электронной почты.
    @Test
    void shouldFailWithEmptyEmail() {
        User invalidUser = new User();
        invalidUser.setLogin("User");
        invalidUser.setBirthday(LocalDate.of(1995, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(invalidUser);
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Адрес электронной почты не может быть пустым.");
    }

    //Тестирование добавления пользователя с некорректным форматом адреса электронной почты.
    @Test
    void shouldFailWithInvalidEmailFormat() {
        User invalidUser = new User();
        invalidUser.setEmail("invalid_email_format");
        invalidUser.setLogin("User");
        invalidUser.setBirthday(LocalDate.of(1995, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(invalidUser);
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Электронная почта должна соответствовать стандартному формату.");
    }

    //Тестирование добавления пользователя с будущим днем рождения.
    @Test
    void shouldFailWithFutureBirthday() {
        User invalidUser = new User();
        invalidUser.setEmail("UserWithFutureBirthday@example.com");
        invalidUser.setLogin("User");
        invalidUser.setBirthday(LocalDate.now().plusYears(1));

        Set<ConstraintViolation<User>> violations = validator.validate(invalidUser);
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Дата рождения не может быть в будущем.");
    }

    //Тестирование успешного обновления пользователя.
    @Test
    void shouldUpdateUserSuccessfully() {
        User initialUser = new User();
        initialUser.setEmail("initial.user@example.com");
        initialUser.setLogin("InitialUser");
        initialUser.setBirthday(LocalDate.of(1990, 1, 1));
        User createdUser = controller.addUser(initialUser);

        User updatedUser = new User();
        updatedUser.setId(createdUser.getId());
        updatedUser.setEmail("updated.user@example.com");
        updatedUser.setLogin("UpdatedUser");
        updatedUser.setBirthday(LocalDate.of(1995, 1, 1));

        User result = controller.updateUser(updatedUser);

        assertThat(result.getEmail()).isEqualTo("updated.user@example.com");
        assertThat(result.getLogin()).isEqualTo("UpdatedUser");
        assertThat(result.getBirthday()).isEqualTo(LocalDate.of(1995, 1, 1));
    }

    //Тестирование неудачного обновления несуществующего пользователя.
    @Test
    void shouldFailUpdatingNonexistentUser() {
        User invalidUser = new User();
        invalidUser.setId(Long.MAX_VALUE);      // Некорректный ID
        invalidUser.setEmail("nonexistent.user@example.com");
        invalidUser.setLogin("NonexistentUser");
        invalidUser.setBirthday(LocalDate.of(1995, 1, 1));

        Throwable exception = Assertions.catchThrowableOfType(
                () -> controller.updateUser(invalidUser),
                EntityNotFoundException.class
        );

        assertThat(exception)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Пользователь с таким Id (%d) не найден".formatted(invalidUser.getId()));
    }

    //Тестирование поведения при наличии пустого имени пользователя.
    @Test
    void shouldUseLoginAsNameWhenNameIsEmpty() {
        User user = new User();
        user.setEmail("no.name@example.com");
        user.setLogin("NoNameUser");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = controller.addUser(user);

        assertThat(createdUser.getName()).isEqualTo("NoNameUser");
    }

    //Тестирование поведения при доступе ко всем пользователям.
    @Test
    void shouldReturnAllUsers() {
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setLogin("UserOne");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        controller.addUser(user1);

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("UserTwo");
        user2.setBirthday(LocalDate.of(1995, 1, 1));
        controller.addUser(user2);

        Collection<User> allUsers = controller.getAllUsers();

        assertThat(allUsers).hasSize(2);
        assertThat(allUsers.stream().anyMatch(u -> u.getEmail().equals("user1@example.com"))).isTrue();
        assertThat(allUsers.stream().anyMatch(u -> u.getEmail().equals("user2@example.com"))).isTrue();
    }
}