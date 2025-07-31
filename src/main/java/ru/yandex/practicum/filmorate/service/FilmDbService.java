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
 * Сервис для обработки бизнес-логики, связанной с фильмами.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FilmDbService {

    /**
     * Хранилище фильмов.
     */
    private final FilmStorage filmStorage;
    /**
     * Хранилище пользователей.
     */
    private final UserStorage userStorage;
    /**
     * Репозиторий для работы с жанрами.
     */
    private final GenreDao genreDao;
    /**
     * Репозиторий для работы с рейтингами MPAA.
     */
    private final MpaDao mpaDao;
    /**
     * Репозиторий для работы с лайками.
     */
    private final LikeDao likeDao;

    /**
     * Добавляет лайк фильму от определенного пользователя.
     *
     * @param userId идентификатор пользователя
     * @param filmId идентификатор фильма
     */
    public void addLike(Long userId, Long filmId) {
        checkExistence(userId, filmId);
        likeDao.addLike(userId, filmId);
        log.info("Пользователь с id {} поставил лайк фильму с id {}", userId, filmId);
    }

    /**
     * Удаляет лайк у фильма от определенного пользователя.
     *
     * @param userId идентификатор пользователя
     * @param filmId идентификатор фильма
     */
    public void deleteLike(Long userId, Long filmId) {
        likeDao.deleteLike(userId, filmId);
        log.info("Пользователь с id {} удалил лайк у фильма с id {}", userId, filmId);
    }

    /**
     * Возвращает список популярных фильмов, отсортированных по количеству лайков.
     *
     * @param topNumber количество фильмов для отображения
     * @return список популярных фильмов
     */
    public List<Film> getPopularFilms(int topNumber) {
        return getFilms().stream()
                .sorted(Comparator.comparingInt((Film film) -> {
                    int likes = likeDao.checkLikes(film.getId());
                    return likes < 0 ? Integer.MAX_VALUE : likes; // Негативные значения считаем большими
                }).reversed())
                .limit(topNumber)
                .collect(Collectors.toList());
    }

    /**
     * Создает новый фильм в базе данных.
     *
     * @param film объект фильма для создания
     * @return созданный фильм
     */
    public Film addFilm(Film film) {
        // Единственная проверка валидности фильма
        ValidationUtils.validateFilm(film, mpaDao, genreDao);

        Film addedFilm = filmStorage.addFilm(film);

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            genreDao.updateGenres(addedFilm.getId(), film.getGenres());
        }

        return addedFilm;
    }

    /**
     * Обновляет данные фильма в базе данных.
     *
     * @param film объект фильма с новыми данными
     * @return обновленный фильм
     */
    public Film updateFilm(Film film) {
        Film currentFilm = filmStorage.getFilmById(film.getId());
        if (currentFilm == null) {
            throw new EntityNotFoundException("Фильм с указанным ID не найден");
        }

        Film updatedFilm = filmStorage.updateFilm(film);
        enrichFilmWithDetails(updatedFilm);
        return updatedFilm;
    }

    /**
     * Предоставляет доступ к списку всех фильмов.
     *
     * @return коллекция фильмов
     */
    public Collection<Film> getFilms() {
        return getAllFilms();
    }

    /**
     * Возвращает фильм по его идентификатору.
     *
     * @param id идентификатор фильма
     * @return объект фильма
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
     * Возвращает список всех фильмов c жанрами и рейтингом
     *
     * @return коллекция фильмов
     */
    public Collection<Film> getAllFilms() {
        return filmStorage.getFilms().stream()
                .peek(this::enrichFilmWithDetails)
                .collect(Collectors.toList());
    }

    /**
     * Дополняет фильм информацией о жанрах и рейтинге MPA.
     *
     * @param film объект фильма
     */
    private void enrichFilmWithDetails(Film film) {
        // Получить жанры фильма
        film.setGenres(new HashSet<>(genreDao.getGenresByFilm(film.getId())));

        // Получить рейтинг MPА
        film.setMpa(mpaDao.getMpaById(film.getMpa().getId()));
    }

    /**
     * Проверяет существование пользователя и фильма перед добавлением лайка.
     *
     * @param userId идентификатор пользователя
     * @param filmId идентификатор фильма
     */
    private void checkExistence(Long userId, Long filmId) {
        if (filmStorage.getFilmById(filmId) == null) {
            throw new EntityNotFoundException("Фильма с таким Id не существует");
        }
        if (userStorage.getUserById(userId) == null) {
            throw new EntityNotFoundException("Пользователь с таким Id не существует");
        }
    }

    /**
     * Удаление фильма по id.
     *
     * @param id идентификатор фильма
     */
    public void deleteFilmById(long id) {
        filmStorage.deleteById(id);
    }

    /**
     * Метод предоставляет список фильмов которые понравились пользователю.
     *
     * @param id id пользователя для которого выгружаются понравившиеся фильмы.
     * @return возвращает список понравившихся фильмов.
     */
    public Collection<Film> getFilmsByUser(Long id) {
        Collection<Film> films = filmStorage.getFilmsByUser(id);
        for (Film film : films) {
            film.setGenres(filmStorage.getGenresByFilm(film.getId()));
            film.setMpa(mpaDao.getMpaById(film.getMpa().getId()));
        }
        return films;
    }
}