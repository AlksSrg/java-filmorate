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
 * Контроллер для работы с жанрами фильмов.
 * Обеспечивает REST API для доступа к информации о жанрах.
 * Поддерживает операции:
 * - Получение жанра по идентификатору
 * - Получение полного списка жанров
 * <p>
 * Все методы работают с сущностью {@link Genre} и используют {@link GenreDbService} для бизнес-логики.
 */

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final Logger logger = LoggerFactory.getLogger(GenreController.class);
    private final GenreDbService genreService;

    /**
     * Возвращает жанр по заданному идентификатору.
     *
     * @param id идентификатор жанра
     * @return объект жанра
     */
    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable Integer id) {
        logger.debug("Получение жанра с ID={}", id);
        return genreService.getGenreById(id);
    }

    /**
     * Возвращает коллекцию всех жанров.
     *
     * @return набор объектов жанров
     */
    @GetMapping
    public Set<Genre> getGenres() {
        logger.debug("Запрос всей коллекции жанров");
        return genreService.getGenres();
    }
}