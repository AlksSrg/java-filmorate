package ru.yandex.practicum.filmorate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.filmorate.storage.dao.like.LikeDao;
import ru.yandex.practicum.filmorate.utils.FilmPopularityComparator;

import java.util.Map;

/**
 * Конфигурационный класс Spring для работы с компаратором популярности фильмов.
 * Создает и настраивает бины, необходимые для работы приложения.
 */

@Configuration
public class AppConfig {

    @Bean
    public FilmPopularityComparator filmPopularityComparator(LikeDao likeDao) {
        Map<Long, Integer> likesCountMap = likeDao.getLikesCountForAllFilms();
        return new FilmPopularityComparator(likesCountMap);
    }
}