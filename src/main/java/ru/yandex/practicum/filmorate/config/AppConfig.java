package ru.yandex.practicum.filmorate.config;

import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.filmorate.storage.dao.like.LikeDao;
import ru.yandex.practicum.filmorate.utils.FilmPopularityComparator;

/**
 * Конфигурация для работы с FilmPopularityComparator
 */
@Configuration
public class AppConfig {

    @Bean
    public FilmPopularityComparator filmPopularityComparator(LikeDao likeDao) {
        Map<Long, Integer> likesCountMap = likeDao.getLikesCountForAllFilms();
        return new FilmPopularityComparator(likesCountMap);
    }
}