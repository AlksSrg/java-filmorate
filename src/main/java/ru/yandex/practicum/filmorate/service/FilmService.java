package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.utils.ValidationUtils;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {


    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    // Добавление нового фильма
    public Film addFilm(Film film) {
        try {
            if (!ValidationUtils.isValidName(film.getName())) { // Используем общий метод проверки даты
                throw new ValidationException("Название фильма обязательно");
            }
            if (!ValidationUtils.isValidReleaseDate(film.getReleaseDate())) { // Используем общий метод проверки даты
                throw new ValidationException("Дата выпуска фильма не должна быть раньше 28 декабря 1895 года");
            }
            filmStorage.addFilm(film);
            log.info("Фильм {} успешно добавлен с ID {}", film.getName(), film.getId());
            return film;
        } catch (ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    // Получение списка всех фильмов
    public Collection<Film> getAllFilms() {
        log.info("Получены все фильмы.");
        return filmStorage.getAllFilms();
    }

    // Получение фильма по Id
    public Film getFilmById(Long filmId) {
        log.info("Получение фильма с ID={}.", filmId);
        return filmStorage.findFilmById(filmId).orElseThrow(() -> new EntityNotFoundException("Фильм с таким Id (%d) не найден".formatted(filmId)));
    }

    // Обновление информации о фильме
    public Film updateFilm(Film film) {
        log.info("Обновление информации о фильме {}", film.getName());
        return filmStorage.updateFilm(film).orElseThrow(() -> new EntityNotFoundException("Фильм с таким Id (%d) не найден".formatted(film.getId())));
    }

    // Постановка лайка фильму
    public void addLike(Long filmId, Long userId) {
        if (filmStorage.findFilmById(filmId).isEmpty()) {
            throw new EntityNotFoundException("Фильма с таким Id не существует");
        }
        if (userStorage.findUserById(userId).isEmpty()) {
            throw new EntityNotFoundException("Пользователь с таким Id не существует");
        }
        filmStorage.addLike(filmId, userId);
        log.info("Пользователь {} поставил лайк фильму с ID={}.", userId, filmId);
    }

    // Удаление лайка у фильма
    public void removeLike(Long filmId, Long userId) {
        if (filmStorage.findFilmById(filmId).isEmpty()) {
            throw new EntityNotFoundException("Фильма с таким Id не существует");
        }
        if (userStorage.findUserById(userId).isEmpty()) {
            throw new EntityNotFoundException("Пользователь с таким Id не существует");
        }
        filmStorage.removeLike(filmId, userId);
        log.info("Пользователь {} убрал лайк у фильма с ID={}.", userId, filmId);
    }

    // Получение топовых фильмов по количеству лайков
    public Collection<Film> getTopRatedFilms(int count) {
        int limit = Math.max(count, 10);
        log.info("Получены лучшие фильмы по рейтингу (топ {})", limit);
        return filmStorage.getTopFilms(count);
    }
}