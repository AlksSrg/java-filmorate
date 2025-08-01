package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.mpa.MpaDao;

import java.util.Collection;

/**
 * Репозиторий для операций с рейтингами MPA.
 */
@Service
@RequiredArgsConstructor
public class MpaDbService {

    /**
     * Поле для доступа к операциям с рейтингом
     */
    private final MpaDao mpaDao;

    /**
     * Возвращает рейтинг MPA по его идентификатору.
     *
     * @param id идентификатор рейтинга
     * @return объект рейтинга MPA
     * @throws EntityNotFoundException если рейтинг не найден
     */
    public Mpa getMpaById(Integer id) {
        try {
            return mpaDao.getMpaById(id);
        } catch (EmptyResultDataAccessException exception) {
            throw new EntityNotFoundException(String.format("Рейтинг с id %s не существует", id));
        }
    }

    /**
     * Возвращает список всех рейтингов MPA.
     *
     * @return коллекция рейтингов MPA
     */
    public Collection<Mpa> getListMpa() {
        return mpaDao.getListMpa();
    }
}