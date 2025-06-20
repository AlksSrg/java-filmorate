package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("/films")
@Validated
@Slf4j
public class FilmController {

    private final FilmService filmService;

    // Получение фильма по Id
    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        return filmService.getFilmById(id);
    }

    // Получение списка всех фильмов
    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    // Добавление нового фильма
    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    // Обновления информации о фильме
    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film updatedFilm) {
        return filmService.updateFilm(updatedFilm);
    }

    // Добавление лайка
    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(@PathVariable Long filmId, @PathVariable Long userId) {
        filmService.addLike(filmId, userId);
    }

    // Удаление Лайка
    @DeleteMapping("/{filmId}/like/{userId}")
    public void removeLike(@PathVariable Long filmId, @PathVariable Long userId) {
        filmService.removeLike(filmId, userId);
    }


    // Получение топ фильмов по количеству лайков
    @GetMapping("/popular")
    public Collection<Film> getTopRatedFilms(int count) {
        return filmService.getTopRatedFilms(count);
    }
}