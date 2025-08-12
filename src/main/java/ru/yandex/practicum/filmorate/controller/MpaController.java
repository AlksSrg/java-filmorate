package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaDbService;

import java.util.Collection;

/**
 * Контроллер для работы с возрастными рейтингами (MPA).
 * Обеспечивает REST API для доступа к информации о рейтингах.
 * Поддерживает операции:
 * - Получение рейтинга по идентификатору
 * - Получение полного списка рейтингов
 * <p>
 * Все методы работают с сущностью {@link Mpa} и используют {@link MpaDbService} для бизнес-логики.
 */

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {

    private final Logger logger = LoggerFactory.getLogger(MpaController.class);
    private final MpaDbService mpaService;

    /**
     * Возвращает рейтинг MPA по уникальному идентификатору.
     *
     * @param id идентификатор рейтинга
     * @return объект рейтинга MPA
     */
    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable Integer id) {
        logger.info("Получение рейтинга с ID={}", id);
        return mpaService.getMpaById(id);
    }

    /**
     * Возвращает полный список всех рейтингов MPA.
     *
     * @return коллекция объектов рейтингов MPA
     */
    @GetMapping
    public Collection<Mpa> getMpaList() {
        logger.info("Получение полного списка рейтингов");
        return mpaService.getListMpa();
    }
}