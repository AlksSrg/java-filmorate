package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Validated
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);

    // Получение списка всех фильмов
    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    // Добавление нового фильма
    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        if (film.getName() == null || film.getName().isEmpty()) {
            throw new ValidationException("Имя фильма обязательно.");
        }
        if (film.getReleaseDate() == null) {
            throw new ValidationException("Дата выпуска обязательна.");
        }
        if (!film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата выпуска фильма не должна быть раньше 28 декабря 1895 года");
        }
        film.setId(getNextFilmId());
        films.put(film.getId(), film);
        log.info("Фильм {} успешно добавлен с ID {}", film.getName(), film.getId());
        return film;
    }

    // Обновления информации о фильме
    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film updatedFilm) {
        if (!films.containsKey(updatedFilm.getId())) {
            throw new ValidationException("Фильма с таким ID не существует");
        }
        films.put(updatedFilm.getId(), updatedFilm);
        log.info("Информация о фильме {} была успешно обновлена", updatedFilm.getName());
        return updatedFilm;
    }

    //Метод для генерации уникально id
    private Long getNextFilmId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}