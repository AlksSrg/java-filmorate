package ru.yandex.practicum.filmorate.utils;

import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.Film;

/**
 * Утилита для проверки соответствия фильма критериям поиска
 */
@NoArgsConstructor
public class FilmSearchMatcher {

    /**
     * Проверяет, соответствует ли фильм критериям поиска
     *
     * @param film         фильм для проверки
     * @param query        поисковый запрос (в нижнем регистре)
     * @param searchParams параметры поиска
     * @return true если фильм соответствует хотя бы одному критерию поиска
     */
    public static boolean matches(Film film, String query, String[] searchParams) {
        for (String param : searchParams) {
            if ("title".equals(param) && film.getName().toLowerCase().contains(query)) {
                return true;
            }
            if ("director".equals(param) && film.getDirectors().stream()
                .anyMatch(d -> d.getName().toLowerCase().contains(query))) {
                return true;
            }
        }
        return false;
    }
}