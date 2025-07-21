package ru.yandex.practicum.filmorate.storage.dao.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashSet;
import java.util.Set;

/**
 * Интерфейс для работы с логикой касающиеся жанров
 */
public interface GenreDao {

    /**
     * Метод получения жанра по его идентификатору
     */
    Genre getGenreById(Integer id);

    /**
     * Метод получает коллекцию жанров в LinkedHashSet.
     */
    Set<Genre> getGenres();

    /**
     * Метод добавления коллекции жанров фильму
     */
    void addGenres(Long filmId, HashSet<Genre> genres);

    /**
     * Метод обновления коллекции жанров у фильма
     */
    void updateGenres(Long filmId, HashSet<Genre> genres);

    /**
     * Метод для получения жанров конкретного фильма
     */
    Set<Genre> getGenresByFilm(Long filmId);
}