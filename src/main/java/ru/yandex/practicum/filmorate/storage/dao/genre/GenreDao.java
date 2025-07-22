package ru.yandex.practicum.filmorate.storage.dao.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashSet;
import java.util.Set;

/**
 * Интерфейс для работы с жанрами фильмов.
 */
public interface GenreDao {

    /**
     * Возвращает жанр по его идентификатору.
     *
     * @param id идентификатор жанра
     * @return объект жанра
     */
    Genre getGenreById(Integer id);

    /**
     * Возвращает коллекцию всех жанров.
     *
     * @return набор жанров
     */
    Set<Genre> getGenres();

    /**
     * Присваивает фильму новую коллекцию жанров.
     *
     * @param filmId идентификатор фильма
     * @param genres набор новых жанров
     */
    void addGenres(Long filmId, HashSet<Genre> genres);

    /**
     * Обновляет существующие жанры фильма новой коллекцией.
     *
     * @param filmId идентификатор фильма
     * @param genres новая коллекция жанров
     */
    void updateGenres(Long filmId, HashSet<Genre> genres);

    /**
     * Возвращает жанры, присвоенные определенному фильму.
     *
     * @param filmId идентификатор фильма
     * @return набор жанров фильма
     */
    Set<Genre> getGenresByFilm(Long filmId);
}