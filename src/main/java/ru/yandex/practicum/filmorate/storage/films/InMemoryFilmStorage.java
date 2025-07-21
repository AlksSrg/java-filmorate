package ru.yandex.practicum.filmorate.storage.films;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * Класс-хранилище реализующий интерфейс для хранения и обновления фильмов
 */
@Component("InMemoryFilmStorage")
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    /**
     * Поле хранилище фильмов
     */
    private final Map<Long, Film> films = new HashMap<>();

    /**
     * Поле счетчик идентификаторов фильмов
     */
    private Long id = 1L;

    /**
     * Метод добавление фильма.
     */
    public Film addFilms(Film film) {
        if (film == null) {
            throw new IllegalArgumentException("Фильм не может быть null");
        }
        log.debug("Фильм добавлен");
        film.setId(id++);
        films.put(film.getId(), film);
        return film;
    }

    /**
     * Метод обновления фильма.
     */
    public Film updateFilms(Film film) {
        Long filmId = film.getId();
        if (films.containsKey(filmId)) {
            log.debug("Фильм обновлен");
            films.put(filmId, film);
        } else {
            log.debug(String.format("Фильм с id %s не существует", filmId));
            throw new EntityNotFoundException("Данного фильма нет в базе данных");
        }
        return film;
    }

    /**
     * Метод получения списка фильмов.
     */
    public Collection<Film> getFilm() {
        log.debug("Запрошен список фильмов, их количество: {} ", films.size());
        return films.values();
    }

    /**
     * Метод получения фильма по id.
     */
    public Film getFilmById(Long id) {
        if (films.containsKey(id)) {
            log.debug("Запрошен фильм с id : {} ", id);
            return films.get(id);
        } else {
            log.debug("Фильм не существует");
            throw new EntityNotFoundException(String.format("Фильм с id %s не существует", id));
        }
    }

    /**
     * Метод для полноценной имплементации интерфейса
     */
    @Override
    public TreeSet<Genre> getGenresByFilm(Long filmId) {
        // Тут временно возвращаем null, пока не будет полной реализации
        return null;
    }
}