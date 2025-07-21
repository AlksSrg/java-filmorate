package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.genre.GenreDao;
import ru.yandex.practicum.filmorate.storage.dao.like.LikeDao;
import ru.yandex.practicum.filmorate.storage.dao.mpa.MpaDao;
import ru.yandex.practicum.filmorate.storage.films.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.utils.ValidationUtils;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс-сервис с логикой для оперирования фильмами с хранилищами <b>filmDbStorage<b/> и <b>userDbStorage<b/>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FilmDbService {

    /**
     * Поле с прошлой версией хранилища фильмов
     */
    private final FilmStorage filmStorage;
    /**
     * Поле с прошлой версией хранилища пользователей
     */
    private final UserStorage userStorage;
    /**
     * Поле для доступа к операциям с жанрами
     */
    private final GenreDao genreDao;
    /**
     * Поле для доступа к операциям с рейтингом
     */
    private final MpaDao mpaDao;
    /**
     * Поле для доступа к операциям с лайками
     */
    private final LikeDao likeDao;

    /**
     * Добавление лайка фильму.
     */
    public void addLike(Long userId, Long filmId) {
        checkExistence(userId, filmId);
        likeDao.addLike(userId, filmId);
        log.info("Пользователь с id {} поставил лайк фильму с id {}", userId, filmId);
    }

    /**
     * Удаление лайка у фильма.
     */
    public void deleteLike(Long userId, Long filmId) {
        likeDao.deleteLike(userId, filmId);
        log.info("Пользователь с id {} удалил лайк у фильма с id {}", userId, filmId);
    }

    /**
     * Возвращает топ фильмов по лайкам.
     */
    public List<Film> getPopularFilms(int topNumber) {
        return getFilm().stream()
                .sorted(Comparator.comparingInt((Film film) -> {
                    int likes = likeDao.checkLikes(film.getId());
                    return likes < 0 ? Integer.MAX_VALUE : likes; // Негативные значения считаем большими
                }).reversed())
                .limit(topNumber)
                .collect(Collectors.toList());
    }

    /**
     * Метод создает новый фильм в БД
     */
    public Film addFilms(Film film) {
        // Единственная проверка валидности фильма
        ValidationUtils.validateFilm(film, mpaDao, genreDao);

        Film addedFilm = filmStorage.addFilms(film);

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            genreDao.updateGenres(addedFilm.getId(), film.getGenres());
        }

        return addedFilm;
    }

    /**
     * Метод обновляет данные в БД о фильме
     */
    public Film updateFilm(Film film) {
        Film currentFilm = filmStorage.getFilmById(film.getId());
        if (currentFilm == null) {
            throw new EntityNotFoundException("Фильм с указанным ID не найден");
        }

        Film updatedFilm = filmStorage.updateFilms(film);
        enrichFilmWithDetails(updatedFilm);
        return updatedFilm;
    }

    /**
     * Метод предоставляет доступ к методу запроса фильмов из хранилища фильмов
     */
    public Collection<Film> getFilm() {
        return getAllFilms();
    }

    /**
     * Метод предоставляет доступ к методу получения фильма из хранилища фильмов по id
     */
    public Film getFilmById(Long id) {
        try {
            Film film = filmStorage.getFilmById(id);
            enrichFilmWithDetails(film);
            return film;
        } catch (EmptyResultDataAccessException exception) {
            throw new EntityNotFoundException(String.format("Фильма с id %s не существует", id));
        }
    }

    /**
     * Метод для получения всех фильмов
     */
    public Collection<Film> getAllFilms() {
        return filmStorage.getFilm().stream()
                .peek(this::enrichFilmWithDetails)
                .collect(Collectors.toList());
    }

    /**
     * Внутренний метод для обогащения фильма дополнительными данными (жанры и рейтинг MPА)
     */
    private void enrichFilmWithDetails(Film film) {
        // Получить жанры фильма
        film.setGenres(new HashSet<>(genreDao.getGenresByFilm(film.getId())));

        // Получить рейтинг MPА
        film.setMpa(mpaDao.getMpaById(film.getMpa().getId()));
    }

    /**
     * Проверяет существование пользователя и фильма перед добавлением лайка
     */
    private void checkExistence(Long userId, Long filmId) {
        if (filmStorage.getFilmById(filmId) == null) {
            throw new EntityNotFoundException("Фильма с таким Id не существует");
        }
        if (userStorage.getUserById(userId) == null) {
            throw new EntityNotFoundException("Пользователь с таким Id не существует");
        }
    }
}