package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.Collection;

/**
 * Сервис для работы с режиссерами фильмов.
 * Обеспечивает бизнес-логику для операций CRUD с режиссерами.
 * Взаимодействует с {@link DirectorStorage} для хранения данных.
 *
 * <p>Поддерживаемые операции:</p>
 * <ul>
 *   <li>Получение списка всех режиссеров</li>
 *   <li>Получение режиссера по ID</li>
 *   <li>Создание, обновление и удаление режиссеров</li>
 * </ul>
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectorDbService {

    /**
     * Хранилище режиссёров.
     */
    private final DirectorStorage directorStorage;

    /**
     * Возвращает список всех зарегистрированных режиссёров.
     *
     * @return коллекция объектов {@link Director}, хранящихся в базе данных
     */
    public Collection<Director> getDirectors() {
        log.info("Запрошены все режиссёры из базы данных.");
        return directorStorage.getDirectors();
    }

    /**
     * Возвращает режиссёра по его идентификатору.
     *
     * @param id идентификатор режиссера
     * @return объект режиссер
     */
    public Director getDirectorById(Long id) {
        log.info("Получение режиссёра c id {} из базы данных.", id);
        Director director = directorStorage.getDirectorById(id);
        if (director == null) {
            throw new EntityNotFoundException(String.format("Режиссера с таким id - %s не существует.", id));
        }
        return director;
    }

    /**
     * Создает нового режиссёра в базе данных.
     *
     * @param director объект режиссёра для создания
     * @return созданный режиссер
     */
    public Director createDirector(Director director) {
        log.info("Добавление нового режиссёра в базу данных.");
        return directorStorage.createDirector(director);
    }

    /**
     * Обновляет информацию о режиссёре.
     *
     * @param director объект режиссёра с обновлёнными данными
     * @return обновлённый режиссёр
     */
    public Director updateDirector(Director director) {
        log.info("Обновление режиссёра в базе данных.");
        return directorStorage.updateDirector(director);
    }

    /**
     * Удаляет режиссера по идентификатору.
     * Если режиссер не найден, операция завершается без ошибки.
     *
     * @param id идентификатор удаляемого режиссера
     */
    public void deleteById(long id) {
        log.info("Удаление режиссёра c id {} из базы данных.", id);
        directorStorage.deleteById(id);
    }
}