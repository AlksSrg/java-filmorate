package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreDbService;

import java.util.Set;

/**
 * Класс-контроллер для получения данных о жанрах.
 */
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final Logger logger = LoggerFactory.getLogger(GenreController.class);
    private final GenreDbService genreService;

    /**
     * Запрос жанра по указанному идентификатору.
     */
    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable Integer id) {
        logger.debug("Получение жанра с ID={}", id);
        return genreService.getGenreById(id);
    }

    /**
     * Запрос всех существующих жанров.
     */
    @GetMapping
    public Set<Genre> getGenres() {
        logger.debug("Запрос всей коллекции жанров");
        return genreService.getGenres();
    }
}