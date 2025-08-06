package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.event.EventDao;
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
import ru.yandex.practicum.filmorate.utils.constants.EventType;
import ru.yandex.practicum.filmorate.utils.constants.Operation;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для работы с фильмами.
 * Реализует комплексную бизнес-логику управления фильмами, включая:
 * <ul>
 *   <li>CRUD операции с фильмами</li>
 *   <li>Управление лайками и рейтингами</li>
 *   <li>Поиск и рекомендации фильмов</li>
 *   <li>Работу с жанрами и режиссерами</li>
 * </ul>
 *
 * <p>Использует:</p>
 * <ul>
 *   <li>{@link FilmStorage} для хранения фильмов</li>
 *   <li>{@link GenreDao}, {@link MpaDao}, {@link DirectorStorage} для связанных данных</li>
 *   <li>{@link LikeDao} для работы с лайками</li>
 * </ul>
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
     * Репозиторий для работы с событиями.
     */
    private final EventDao eventDao;

    /**
     * Добавляет лайк фильму от определенного пользователя.
     *
     * @param userId идентификатор пользователя
     * @param filmId идентификатор фильма
     */
    public void addLike(Long userId, Long filmId) {
        checkExistence(userId, filmId);
        likeDao.addLike(userId, filmId);
        eventDao.addEvent(userId, EventType.LIKE, Operation.ADD, filmId);
        log.info("Пользователь с id {} поставил лайк фильму с id {}", userId, filmId);
    }

    /**
     * Убирает лайк у фильма от конкретного пользователя.
     *
     * @param filmId уникальный идентификатор фильма
     * @param userId уникальный идентификатор пользователя
     */
    public void deleteLikeFilm(Long filmId, Long userId) {
        checkExistence(userId, filmId);
        likeDao.deleteLike(userId, filmId);
        eventDao.addEvent(userId, EventType.LIKE, Operation.REMOVE, filmId);
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
        // Проверка валидности фильма
        ValidationUtils.validateFilm(film, mpaDao, genreDao);

        // Добавляем фильм в хранилище
        Film addedFilm = filmStorage.addFilm(film);

        // Обрабатываем жанры
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            // Сортируем жанры по ID перед сохранением
            Set<Genre> sortedGenres = new TreeSet<>(Comparator.comparing(Genre::getId));
            sortedGenres.addAll(film.getGenres());

            // Обновляем жанры в БД
            genreDao.updateGenres(addedFilm.getId(), sortedGenres);

            // Устанавливаем отсортированные жанры в возвращаемый объект
            addedFilm.setGenres(sortedGenres);
        } else {
            addedFilm.setGenres(Collections.emptySet());
        }

        // Обрабатываем режиссеров
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            directorStorage.addDirectors(film.getId(), film.getDirectors());
            addedFilm.setDirectors(new HashSet<>(film.getDirectors()));
        } else {
            addedFilm.setDirectors(Collections.emptySet());
        }

        // Получаем полные данные MPA
        addedFilm.setMpa(mpaDao.getMpaById(film.getMpa().getId()));

        return addedFilm;
    }

    /**
     * Обновляет данные фильма в базе данных.
     *
     * @param film объект фильма с новыми данными
     * @return обновленный фильм
     */
    public Film updateFilm(Film film) {
        // Проверяем существование фильма
        Film currentFilm = filmStorage.getFilmById(film.getId());
        if (currentFilm == null) {
            throw new EntityNotFoundException("Фильм с указанным ID не найден");
        }

        // Проверяем и корректируем дату релиза
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            film.setReleaseDate(currentFilm.getReleaseDate());
        }

        // Обновляем основную информацию о фильме
        Film updatedFilm = filmStorage.updateFilm(film);

        // Обрабатываем жанры
        if (film.getGenres() != null) {
            // Сортируем жанры по ID перед сохранением
            Set<Genre> sortedGenres = new TreeSet<>(Comparator.comparing(Genre::getId));
            sortedGenres.addAll(film.getGenres());

            genreDao.updateGenres(film.getId(), sortedGenres);
            updatedFilm.setGenres(sortedGenres);
        } else {
            genreDao.updateGenres(film.getId(), Collections.emptySet());
            updatedFilm.setGenres(Collections.emptySet());
        }

        // Обрабатываем режиссеров
        if (film.getDirectors() != null) {
            directorStorage.updateDirectorsForFilm(film.getId(), film.getDirectors());
            updatedFilm.setDirectors(new HashSet<>(film.getDirectors()));
        } else {
            directorStorage.updateDirectorsForFilm(film.getId(), Collections.emptySet());
            updatedFilm.setDirectors(Collections.emptySet());
        }

        // Обогащаем данные MPA
        updatedFilm.setMpa(mpaDao.getMpaById(film.getMpa().getId()));

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
     * Удаляет фильм по id.
     *
     * @param id идентификатор фильма
     */
    public void deleteFilmById(long id) {
        filmStorage.deleteById(id);
    }

    /**
     * Возвращает список фильмов которые понравились пользователю.
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
     * Сортирует фильмы заданного режиссёру по лайкам или годам выпуска.
     *
     * @param directorId id режиссёра чьи фильмы будут сортироваться.
     * @param sort       параметр сортировки year или likes
     * @return возвращает список отсортированных фильмов.
     */
    public Collection<Film> getFilmsByDirector(Long directorId, String sort) {
        if (directorStorage.getDirectorById(directorId) == null) {
            throw new EntityNotFoundException(
                    String.format("Режиссера с таким id - %s не существует.", directorId));
        }
        if (!sort.equals("year") && !sort.equals("likes")) {
            throw new EntityNotFoundException(
                    String.format("Нет сортировки по указанному параметру %s", sort));
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
     * Осуществляет поиск фильмов по названию и/или режиссеру.
     * Поддерживает следующие параметры поиска:
     * - "title" - поиск по названию фильма (без учета регистра)
     * - "director" - поиск по имени режиссера (без учета регистра)
     * - "title, director" - поиск одновременно по названию и режиссеру (по умолчанию)
     *
     * @param query строка поиска (минимум 1 символ)
     * @param by    параметры поиска (должны содержать "title", "director" или оба значения)
     * @return список фильмов, отсортированных по популярности (количеству лайков)
     * @throws IllegalArgumentException если параметры поиска некорректны
     */
    public List<Film> searchFilms(String query, String by) {
        String[] searchParams = SearchParameterParser.parseSearchParameters(by);
        String lowerCaseQuery = query.toLowerCase();

        Collection<Film> allFilms = getAllFilms();

        Map<Long, Integer> likesCountMap = likeDao.getLikesCountForAllFilms();

        Comparator<Film> comparator = new FilmPopularityComparator(likesCountMap);

        return allFilms.stream()
                .filter(film -> FilmSearchMatcher.matches(film, lowerCaseQuery, searchParams))
                .sorted(comparator)
                .collect(Collectors.toList());
    }
}