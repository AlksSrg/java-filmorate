package ru.yandex.practicum.filmorate.utils;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Comparator;
import java.util.Map;

/**
 * Компаратор для сравнения фильмов по популярности (количеству лайков)
 */
public class FilmPopularityComparator implements Comparator<Film> {

    private final Map<Long, Integer> likesCountMap;

    public FilmPopularityComparator(Map<Long, Integer> likesCountMap) {
        this.likesCountMap = likesCountMap;
    }

    @Override
    public int compare(Film film1, Film film2) {
        int likes1 = likesCountMap.getOrDefault(film1.getId(), 0);
        int likes2 = likesCountMap.getOrDefault(film2.getId(), 0);
        return Integer.compare(likes2, likes1); // Сортируем по убыванию
    }
}