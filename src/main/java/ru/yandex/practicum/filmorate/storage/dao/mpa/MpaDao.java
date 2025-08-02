package ru.yandex.practicum.filmorate.storage.dao.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Интерфейс для работы с рейтингами MPA.
 */
public interface MpaDao {

    /**
     * Возвращает рейтинг MPA по его идентификатору.
     *
     * @param id идентификатор рейтинга
     * @return объект рейтинга MPA
     */
    Mpa getMpaById(Integer id);

    /**
     * Возвращает список всех рейтингов MPA.
     *
     * @return список рейтингов MPA
     */
    List<Mpa> getListMpa();

    Map<Long, Mpa> getMpaMapByFilms(Set<Long> filmIds);
}