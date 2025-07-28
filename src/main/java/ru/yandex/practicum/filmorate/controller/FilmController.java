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
     * Добавляет новый фильм в базу данных.
     *
     * @param film объект фильма для добавления
     * @return созданный объект фильма
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Добавление нового фильма {}", film.getName());
        return filmService.addFilm(film);
    }

    /**
     * Обновляет существующий фильм в базе данных.
     *
     * @param film обновляемый объект фильма
     * @return обновленный объект фильма
     */
    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Обновление фильма с id={}", film.getId());
        return filmService.updateFilm(film);
    }

    /**
     * Ставит лайк фильму от конкретного пользователя.
     *
     * @param filmId уникальный идентификатор фильма
     * @param userId уникальный идентификатор пользователя
     * @return пустой ответ с успешным статусом
     */
    @PutMapping("/{film_id}/like/{user_id}")
    public ResponseEntity<Void> addLike(@PathVariable("film_id") Long filmId, @PathVariable("user_id") Long userId) {
        filmService.addLike(userId, filmId);
        return ResponseEntity.noContent().build();
    }


    /**
     * Убирает лайк у фильма от конкретного пользователя.
     *
     * @param filmId уникальный идентификатор фильма
     * @param userId уникальный идентификатор пользователя
     * @return пустой ответ с успешным статусом
     */
    @DeleteMapping("/{film_id}/like/{id}")
    public ResponseEntity<Void> deleteLikeFilm(@PathVariable("film_id") Long filmId, @PathVariable("id") Long userId) {
        filmService.deleteLike(filmId, userId);
        log.info("У фильма с id={} удален лайк от пользователя id={}", filmId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Получает полный список всех фильмов.
     *
     * @return коллекция объектов Film
     */
    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    /**
     * Получает фильм по указанному идентификатору.
     *
     * @param id идентификатор запрашиваемого фильма
     * @return объект фильма
     */
    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        return filmService.getFilmById(id);
    }

    /**
     * Получает список наиболее популярных фильмов.
     *
     * @param count количество возвращаемых фильмов (по умолчанию — 10)
     * @return коллекция популярных фильмов
     */
    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(value = "count", defaultValue = "10") Integer count) {
        return filmService.getPopularFilms(count);
    }
}