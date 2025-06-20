package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Optional<Film> findFilmById(Long id);

    Collection<Film> getAllFilms();

    Film addFilm(Film film);

    Optional<Film> updateFilm(Film updatedFilm);

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    Collection<Film> getTopFilms(int topLimit);
}