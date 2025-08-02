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

    Set<Director> getDirectorsByFilmId(Long filmId);

    void addDirectors(Long filmId, Set<Director> directors);

    void deleteDirectorsByFilmId(Long filmId);

    void updateDirectorsForFilm(Long filmId, Set<Director> directors);

    Map<Long, Set<Director>> getAllDirectorsMap();

    Map<Long, Set<Director>> getDirectorMapByFilms(Set<Long> filmIds);
}
