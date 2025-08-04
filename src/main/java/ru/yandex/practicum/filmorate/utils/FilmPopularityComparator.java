package ru.yandex.practicum.filmorate.utils;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.like.LikeDao;

import java.util.Comparator;

/**
 * Компаратор для сравнения фильмов по популярности (количеству лайков)
 */
public class FilmPopularityComparator implements Comparator<Film> {
    private final LikeDao likeDao;

    public FilmPopularityComparator(LikeDao likeDao) {
        this.likeDao = likeDao;
    }

    @Override
    public int compare(Film film1, Film film2) {
        int likes1 = likeDao.getLikesCount(film1.getId());
        int likes2 = likeDao.getLikesCount(film2.getId());
        return Integer.compare(likes2, likes1);
    }
}