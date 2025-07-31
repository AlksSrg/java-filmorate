package ru.yandex.practicum.filmorate.storage.films;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Set;

/**
 * Интерфейс для работы с хранилищем фильмов.
 */
public interface FilmStorage {
    /**
     * Сохраняет новый фильм в хранилище.
     *
     * @param film объект фильма для сохранения
     * @return сохранённый фильм
     */
    Film addFilm(Film film);

    /**
     * Обновляет информацию о фильме в хранилище.
     *
     * @param film объект фильма с обновлёнными данными
     * @return обновлённый фильм
     */
    Film updateFilm(Film film);

    /**
     * Возвращает коллекцию всех фильмов.
     *
     * @return коллекция фильмов
     */
    Collection<Film> getFilms();

    /**
     * Возвращает фильм по его идентификатору.
     *
     * @param id идентификатор фильма
     * @return объект фильма
     */
    Film getFilmById(Long id);

    /**
     * Возвращает набор жанров, относящихся к данному фильму.
     *
     * @param filmId идентификатор фильма
     * @return набор жанров фильма
     */
    Set<Genre> getGenresByFilm(Long filmId);

    /**
     * Удаляет фильм по идентификатору
     *
     * @param id идентификатор фильма
     */
    void deleteById(long id);

    /**
     * Метод предоставляет список фильмов которые понравились пользователю.
     *
     * @param id id пользователя для которого выгружаются понравившиеся фильмы.
     * @return возвращает список понравившихся фильмов.
     */
    Collection<Film> getFilmsByUser(Long id);
}