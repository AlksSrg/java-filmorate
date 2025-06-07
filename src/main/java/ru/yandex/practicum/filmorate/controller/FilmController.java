package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utils.ValidationUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Validated
@Slf4j
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    // Получение списка всех фильмов
    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    // Добавление нового фильма
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film addFilm(@Valid @RequestBody Film film) {
        try {
            if (!ValidationUtils.isValidReleaseDate(film.getReleaseDate())) { // Используем общий метод проверки даты
                throw new ValidationException("Дата выпуска фильма не должна быть раньше 28 декабря 1895 года");
            }
            ValidationUtils.validate(film);
            film.setId(getNextFilmId());
            films.put(film.getId(), film);
            log.info("Фильм {} успешно добавлен с ID {}", film.getName(), film.getId());
            return film;
        } catch (ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
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