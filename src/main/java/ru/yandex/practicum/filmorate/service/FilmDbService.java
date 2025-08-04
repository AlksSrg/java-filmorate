package ru.yandex.practicum.filmorate.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.genre.GenreDao;
import ru.yandex.practicum.filmorate.storage.dao.like.LikeDao;
import ru.yandex.practicum.filmorate.storage.dao.mpa.MpaDao;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.films.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.utils.FilmPopularityComparator;
import ru.yandex.practicum.filmorate.utils.FilmSearchMatcher;
import ru.yandex.practicum.filmorate.utils.SearchParameterParser;
import ru.yandex.practicum.filmorate.utils.ValidationUtils;

import java.time.LocalDate;
import java.util.*;
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
     * Репозиторий для работы с рейтингами MPA.
     */
    private final MpaDao mpaDao;
    /**
     * Репозиторий для работы с лайками.
     */
    private final LikeDao likeDao;
    /**
     * Утилита для сравнения фильмов по популярности
     */
    private final FilmPopularityComparator filmPopularityComparator;

    /**
     * Репозиторий для работы с режиссёрами.
     */
    private final DirectorStorage directorStorage;

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
     * Убирает лайк у фильма от конкретного пользователя.
     *
     * @param filmId уникальный идентификатор фильма
     * @param userId уникальный идентификатор пользователя
     */
    public void deleteLikeFilm(Long filmId, Long userId) {
        likeDao.deleteLike(userId, filmId);
        log.info("У фильма с id={} удален лайк от пользователя id={}", filmId, userId);
    }

    /**
     * Возвращает список популярных фильмов, отсортированных по количеству лайков.
     *
     * @param topNumber количество фильмов для отображения
     * @param genreId   идентификатор жанра для фильтрации
     * @param year      год выпуска фильма для фильтрации
     * @return список популярных фильмов
     */
    public List<Film> getPopularFilms(int topNumber, Integer genreId, Integer year) {
        if (topNumber <= 0) {
            throw new IllegalArgumentException("Количество фильмов должно быть больше 0");
        }

        List<Film> films = new ArrayList<>(filmStorage.getFilteredFilms(genreId, year));
        return films.stream()
            .sorted(Comparator.comparingInt((Film film) -> {
                    int likes = likeDao.checkLikes(film.getId());
                    return likes < 0 ? 0 : likes;
                }).reversed()
                .thenComparing(Film::getReleaseDate, Comparator.reverseOrder()))
            .limit(topNumber)
            .peek(this::enrichFilmWithDetails)
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

        if (film.getDirectors() != null) {
            directorStorage.addDirectors(film.getId(), film.getDirectors());
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

        LocalDate releaseDate = film.getReleaseDate();
        if (releaseDate == null || releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            film.setReleaseDate(currentFilm.getReleaseDate());
        }


        Film updatedFilm = filmStorage.updateFilm(film);

        genreDao.updateGenres(film.getId(), film.getGenres());
        directorStorage.updateDirectorsForFilm(film.getId(), film.getDirectors());

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
     * Дополняет фильм информацией о жанрах, рейтинге MPA и режиссёрах.
     *
     * @param film объект фильма
     */
    private void enrichFilmWithDetails(Film film) {
        // Получить жанры фильма
        film.setGenres(new HashSet<>(genreDao.getGenresByFilm(film.getId())));

        // Получить рейтинг MPА
        film.setMpa(mpaDao.getMpaById(film.getMpa().getId()));

        //Получить режиссёров
        film.setDirectors(new HashSet<>(directorStorage.getDirectorsByFilmId(film.getId())));
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
            film.setDirectors(directorStorage.getDirectorsByFilmId(film.getId()));
        }
        return films;
    }

    /**
     * Сортировка фильмов заданного режиссёру по лайкам или годам выпуска.
     *
     * @param directorId id режиссёра чьи фильмы будут сортироваться.
     * @param sort       параметр сортировки year или likes
     * @return возвращает список отсортированных фильмов.
     */
    public Collection<Film> getFilmsByDirector(Long directorId, String sort) {
        if (directorStorage.getDirectorById(directorId) == null) {
            throw new EntityNotFoundException(String.format("Режиссера с таким id - %s не существует.", directorId));
        }
        if (!sort.equals("year") && !sort.equals("likes")) {
            throw new EntityNotFoundException(String.format("Нет сортировки по указанному параметру %s", sort));
        }

        Collection<Film> films = filmStorage.getFilmsByDirector(directorId, sort);

        if (films.isEmpty()) {
            return films;
        }

        Set<Long> filmIds = films.stream().map(Film::getId).collect(Collectors.toSet());
        Map<Long, Set<Genre>> genresMap = genreDao.getGenresMapByFilms(filmIds);
        Map<Long, Set<Director>> directorsMap = directorStorage.getDirectorMapByFilms(filmIds);
        Map<Long, Mpa> mpaMap = mpaDao.getMpaMapByFilms(filmIds);

        for (Film film : films) {
            film.setDirectors(directorsMap.get(film.getId()));
            film.setGenres(genresMap.get(film.getId()));
            film.setMpa(mpaMap.get(film.getId()));
        }

        return films;
    }

    /**
     * Возвращает список общих фильмов между двумя пользователями, отсортированных по популярности.
     *
     * @param userId   идентификатор первого пользователя
     * @param friendId идентификатор второго пользователя
     * @return список общих фильмов, отсортированных по количеству лайков
     * @throws EntityNotFoundException если один из пользователей не найден
     */
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        // Проверяем существование пользователей
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);

        // Получаем ID фильмов с лайками каждого пользователя
        Set<Long> userLikedFilms = likeDao.getLikedFilms(userId);
        Set<Long> friendLikedFilms = likeDao.getLikedFilms(friendId);

        // Находим пересечение
        Set<Long> commonFilmIds = userLikedFilms.stream()
            .filter(friendLikedFilms::contains)
            .collect(Collectors.toSet());

        if (commonFilmIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Получаем все фильмы за один запрос
        List<Film> films = filmStorage.getFilmsByIds(commonFilmIds);

        // Получаем количество лайков для всех фильмов
        Map<Long, Integer> likesCountMap = commonFilmIds.stream()
            .collect(Collectors.toMap(
                filmId -> filmId,
                likeDao::getLikesCount
            ));

        return films.stream()
            .peek(this::enrichFilmWithDetails)
            .sorted(Comparator.comparingInt((Film film) ->
                    likesCountMap.getOrDefault(film.getId(), 0))
                .reversed())
            .collect(Collectors.toList());
    }

    /**
     * Реализует поиск фильмов по названию и/или режиссёру с сортировкой по популярности
     *
     * @param query текст для поиска (без учета регистра)
     * @param by    параметры поиска: "director" (по режиссёру), "title" (по названию), или оба
     *              значения через запятую (по умолчанию: "title,director")
     * @return список фильмов, соответствующих критериям поиска, отсортированных по популярности
     * @throws IllegalArgumentException если параметр 'by' содержит недопустимые значения
     */
    public List<Film> searchFilms(String query, String by) {
        String[] searchParams = SearchParameterParser.parseSearchParameters(by);
        String lowerCaseQuery = query.toLowerCase();

        return getAllFilms().stream()
            .filter(film -> FilmSearchMatcher.matches(film, lowerCaseQuery, searchParams))
            .sorted(filmPopularityComparator)
            .collect(Collectors.toList());
    }
}