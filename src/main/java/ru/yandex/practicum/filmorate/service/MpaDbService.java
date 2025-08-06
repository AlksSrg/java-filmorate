package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.mpa.MpaDao;

import java.util.Collection;

/**
 * Сервис для работы с возрастными рейтингами MPA.
 * Обеспечивает доступ к информации о рейтингах.
 *
 * <p>Поддерживаемые операции:</p>
 * <ul>
 *   <li>Получение рейтинга по ID</li>
 *   <li>Получение списка всех рейтингов</li>
 * </ul>
 *
 * <p>Использует {@link MpaDao} для доступа к данным.</p>
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