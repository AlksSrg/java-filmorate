package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.genre.GenreDao;

import java.util.Set;

/**
 * Класс-сервис с логикой для работы жанрами
 */
@Service
@RequiredArgsConstructor
public class GenreDbService {

    /**
     * Поле для доступа к операциям с жанрами
     */
    private final GenreDao genreDao;

    /**
     * Метод получает жанр по его идентификатору
     */
    public Genre getGenreById(Integer id) {
        if (id == null) {
            throw new EntityNotFoundException(String.format("Жанра с id %s не существует", id));
        }
        return genreDao.getGenreById(id);
    }

    /**
     * Метод получает коллекцию жанров
     */
    public Set<Genre> getGenres() {
        return genreDao.getGenres();
    }
}