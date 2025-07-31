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
 * Реализация хранилища фильмов в памяти.
 */
@Component("InMemoryFilmStorage")
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    /**
     * Хранит фильмы в виде карты (ключ — идентификатор фильма, значение — объект фильма).
     */
    private final Map<Long, Film> films = new HashMap<>();

    /**
     * Генерирует уникальные идентификаторы фильмов.
     */
    private Long id = 1L;

    /**
     * Добавляет новый фильм в хранилище.
     *
     * @param film объект фильма для добавления
     * @return добавленный фильм
     */
    public Film addFilm(Film film) {
        if (film == null) {
            throw new IllegalArgumentException("Фильм не может быть null");
        }
        log.debug("Фильм добавлен");
        film.setId(id++);
        films.put(film.getId(), film);
        return film;
    }

    /**
     * Обновляет информацию о фильме в хранилище.
     *
     * @param film объект фильма с обновлёнными данными
     * @return обновлённый фильм
     */
    public Film updateFilm(Film film) {
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
     * Возвращает коллекцию всех фильмов.
     *
     * @return коллекция фильмов
     */
    public Collection<Film> getFilms() {
        log.debug("Запрошен список фильмов, их количество: {} ", films.size());
        return films.values();
    }

    /**
     * Возвращает фильм по его идентификатору.
     *
     * @param id идентификатор фильма
     * @return объект фильма
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
     * Возврат набора жанров для фильма (метод заглушка).
     *
     * @param filmId идентификатор фильма
     * @return временный объект TreeSet<Genre>
     */
    @Override
    public TreeSet<Genre> getGenresByFilm(Long filmId) {
        // Тут временно возвращаем null, пока не будет полной реализации
        return null;
    }

    /**
     * Пустая реализация метода из интерфейса
     *
     * @param id идентификатор фильма
     */
    @Override
    public void deleteById(long id) {
        log.warn("Использование устаревшей реализации");
        throw new UnsupportedOperationException("Метод не поддерживается в устаревшей реализации");
    }
}