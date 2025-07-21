package ru.yandex.practicum.filmorate.storage.films;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Set;

/**
 * Интерфейс для работы с хранилищем фильмов
 */
public interface FilmStorage {
    /**
     * Метод добавления фильма
     */
    Film addFilms(Film film);

    /**
     * Метод изменения фильма
     */
    Film updateFilms(Film film);

    /**
     * Запрос коллекции фильмов
     */
    Collection<Film> getFilm();

    /**
     * Запрос фильма по id
     */
    Film getFilmById(Long id);

    /**
     * Метод получения жанра по идентификатору фильма
     */
    Set<Genre> getGenresByFilm(Long filmId);
}