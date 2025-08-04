package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Интерфейс для работы с хранилищем режиссёров.
 */
public interface DirectorStorage {

    /**
     * Возвращает коллекцию всех режиссёров.
     *
     * @return коллекция режиссёров
     */
    Collection<Director> getDirectors();

    /**
     * Возвращает режиссёра по его идентификатору.
     *
     * @param id идентификатор режиссёра
     * @return объект режиссёра
     */
    Director getDirectorById(Long id);

    /**
     * Регистрирует нового режиссёра в БД.
     *
     * @param director объект режиссёра для регистрации
     * @return сохранённый в БД режиссёр
     */
    Director createDirector(Director director);

    /**
     * Обновляет информацию о режиссёре в хранилище.
     *
     * @param director объект режиссёра с обновлёнными данными
     * @return обновлённый режиссёр
     */
    Director updateDirector(Director director);

    /**
     * Удаляет режиссёра по идентификатору
     *
     * @param id идентификатор режиссёра
     */
    void deleteById(long id);

    /**
     * Возвращает список режиссеров для заданного фильма
     *
     * @param filmId идентификатор фильма
     * @return список объектов режиссёров
     */
    Set<Director> getDirectorsByFilmId(Long filmId);

    /**
     * Добавляет список режиссёров к заданному фильму
     *
     * @param filmId идентификатор фильма
     * @param directors список объектов режиссёров для данного фильма
     */
    void addDirectors(Long filmId, Set<Director> directors);

    /**
     * Удаление списка режиссеров у фильма
     *
     * @param filmId идентификатор фильма
     */
    void deleteDirectorsByFilmId(Long filmId);

    /**
     * Обновление списка режиссёров к заданному фильму
     *
     * @param filmId идентификатор фильма
     * @param directors новый список объектов режиссёров для данного фильма
     */
    void updateDirectorsForFilm(Long filmId, Set<Director> directors);

    /**
     * Получение информации о режиссерах для всех фильмов в виде таблицы
     *
     * @return таблицу, где ключ - id фильма, значение - список объектов режиссёров
     */
    Map<Long, Set<Director>> getAllDirectorsMap();

    /**
     * Возвращает таблицу режиссёров для заданного списка id фильмов.
     *
     * @param filmIds список id фильмов
     * @return таблицу, где ключ - id фильма, значение - список режиссёров.
     */
    Map<Long, Set<Director>> getDirectorMapByFilms(Set<Long> filmIds);
}
