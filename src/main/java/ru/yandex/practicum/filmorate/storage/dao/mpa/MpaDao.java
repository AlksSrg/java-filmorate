package ru.yandex.practicum.filmorate.storage.dao.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

/**
 * Интерфейс для работы с логикой касающиеся рейтинга
 */
public interface MpaDao {

    /**
     * Запрос рейтинга по идентификатору
     */
    Mpa getMpaById(Integer id);

    /**
     * Запрос списка рейтингов.
     */
    List<Mpa> getListMpa();
}