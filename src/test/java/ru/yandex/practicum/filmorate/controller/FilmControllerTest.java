package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class FilmControllerTest {

    private FilmController controller;

    //создаем экземпляр контроллера перед каждым тестом
    @BeforeEach
    void setUp() {
        controller = new FilmController(new FilmService(new InMemoryFilmStorage(), new InMemoryUserStorage()));
    }

    //Тест получения списка всех фильмов.
    @Test
    void testGetAllFilms() {

        Film film1 = new Film();
        film1.setName("Матрица");
        film1.setReleaseDate(LocalDate.of(1999, 3, 31));
        controller.addFilm(film1);

        Collection<Film> films = controller.getAllFilms();
        assertThat(films).size().isEqualTo(1);
        assertThat(films.stream().anyMatch(f -> f.getName().equals("Матрица"))).isTrue();
    }

    //Тест успешного добавления фильма с корректными данными.
    @Test
    void testAddFilmSuccess() {
        Film film = new Film();
        film.setName("Interstellar");
        film.setReleaseDate(LocalDate.of(2014, 11, 7));

        Film result = controller.addFilm(film);
        assertThat(result.getId()).isGreaterThan(0);
        assertThat(result.getName()).isEqualTo("Interstellar");
        assertThat(result.getReleaseDate()).isEqualTo(LocalDate.of(2014, 11, 7));
    }

    //Тест добавления фильма с некорректной датой выпуска.
    @Test
    void testAddFilmInvalidDate() {
        Film film = new Film();
        film.setName("Old Movie");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        Throwable exception = catchThrowableOfType(() -> controller.addFilm(film), ValidationException.class);
        assertThat(exception).hasMessage("Дата выпуска фильма не должна быть раньше 28 декабря 1895 года");
    }

    //Тест добавления пустого фильма.
    @Test
    void testAddEmptyFilm() {
        Film emptyFilm = new Film();
        Throwable exception = catchThrowableOfType(() -> controller.addFilm(emptyFilm), ValidationException.class);
        assertThat(exception).hasMessage("Название фильма обязательно");
    }

    //Тест успешного обновления фильма.
    @Test
    void testUpdateFilmSuccess() {

        Film originalFilm = new Film();
        originalFilm.setName("Terminator");
        originalFilm.setReleaseDate(LocalDate.of(1984, 10, 26));
        Film addedFilm = controller.addFilm(originalFilm);

        Film updatedFilm = new Film();
        updatedFilm.setId(addedFilm.getId());
        updatedFilm.setName("The Terminator");
        updatedFilm.setReleaseDate(LocalDate.of(1984, 10, 26));

        Film result = controller.updateFilm(updatedFilm);
        assertThat(result.getName()).isEqualTo("The Terminator");
    }

    //Тест обновления несуществующего фильма.
    @Test
    void testUpdateNonexistentFilm() {
        Film nonexistentFilm = new Film();
        nonexistentFilm.setId(999L);
        nonexistentFilm.setName("New Name");
        nonexistentFilm.setReleaseDate(LocalDate.of(2020, 1, 1));

        Throwable exception = catchThrowableOfType(() -> controller.updateFilm(nonexistentFilm), EntityNotFoundException.class);
        assertThat(exception).hasMessage("Фильм с таким Id (%d) не найден".formatted(nonexistentFilm.getId()));
    }
}