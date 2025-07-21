package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmDbService;

import java.util.Collection;

/**
 * Класс-контроллер для работы с фильмами
 */
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final FilmDbService filmService;

    /**
     * Добавляет фильм в хранилище.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Добавление нового фильма {}", film.getName());
        return filmService.addFilms(film);
    }

    /**
     * Обновляет фильм в хранилище.
     */
    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Обновление фильма с id={}", film.getId());
        return filmService.updateFilm(film);
    }

    /**
     * Добавляет лайк фильму
     */
    @PutMapping("/{film_id}/like/{user_id}")
    public ResponseEntity<Void> addLike(@PathVariable("film_id") Long filmId, @PathVariable("user_id") Long userId) {
        filmService.addLike(userId, filmId);
        return ResponseEntity.noContent().build();
    }


    /**
     * Удаляет лайк у фильма
     */
    @DeleteMapping("/{film_id}/like/{id}")
    public ResponseEntity<Void> deleteLikeFilm(@PathVariable("film_id") Long filmId, @PathVariable("id") Long userId) {
        filmService.deleteLike(filmId, userId);
        log.info("У фильма с id={} удален лайк от пользователя id={}", filmId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Запрашивает список всех фильмов
     */
    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    /**
     * Запрашивает фильм по его ID
     */
    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        return filmService.getFilmById(id);
    }

    /**
     * Запрашивает список популярных фильмов
     */
    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(value = "count", defaultValue = "10") Integer count) {
        return filmService.getPopularFilms(count);
    }
}