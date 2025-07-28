package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.genre.GenreDao;

import java.util.Set;

/**
 * Сервис для работы с жанрами фильмов.
 */
@Service
@RequiredArgsConstructor
public class GenreDbService {

    /**
     * Репозиторий для операций с жанрами.
     */
    private final GenreDao genreDao;

    /**
     * Возвращает жанр по его идентификатору.
     *
     * @param id идентификатор жанра
     * @return объект жанра
     * @throws EntityNotFoundException если жанр не найден
     */
    public Genre getGenreById(Integer id) {
        if (id == null) {
            throw new EntityNotFoundException(String.format("Жанра с id %s не существует", id));
        }
        return genreDao.getGenreById(id);
    }

    /**
     * Возвращает список всех жанров.
     *
     * @return коллекция жанров
     */
    public Set<Genre> getGenres() {
        return genreDao.getGenres();
    }
}