package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Getter
@Setter
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private long id = 0;

    // Получение фильма по Id
    @Override
    public Optional<Film> findFilmById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    // Получение списка всех фильмов
    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    // Добавление нового фильма
    @Override
    public Film addFilm(Film film) {
        film.setId(getNextFilmId());
        films.put(film.getId(), film);
        return film;
    }

    // Обновления информации о фильме
    @Override
    public Optional<Film> updateFilm(Film updatedFilm) {
        return Optional.ofNullable(films.computeIfPresent(updatedFilm.getId(), (k, v) -> updatedFilm));
    }

    // Добавление лайка фильму
    @Override
    public void addLike(Long filmId, Long userId) {
        Film film = films.get(filmId);
        if (film != null) {
            film.getLike().add(userId);
        }
    }

    // Удаление лайка у фильма
    @Override
    public void removeLike(Long filmId, Long userId) {
        Film film = films.get(filmId);
        if (film != null) {
            film.getLike().remove(userId);
        }
    }

    // Получение топ фильмов по количеству лайков
    @Override
    public Collection<Film> getTopFilms(int topLimit) {
        return films.values().stream()
                .sorted(Comparator.comparingInt(f -> -(f.getLike().size())))
                .limit(topLimit)
                .collect(Collectors.toList());
    }

    //Метод для генерации уникально id
    private Long getNextFilmId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}