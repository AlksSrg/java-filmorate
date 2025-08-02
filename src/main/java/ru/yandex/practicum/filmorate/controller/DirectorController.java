package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorDbService;

import java.util.Collection;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorDbService directorService;

    /**
     * Получает полный список всех режиссёров.
     *
     * @return коллекция объектов Director
     */
    @GetMapping
    public Collection<Director> getDirectors() {
        return directorService.getDirectors();
    }

    /**
     * Получает режиссёра по указанному идентификатору.
     *
     * @param id идентификатор запрашиваемого режиссёра
     * @return объект режиссёра
     */
    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable @Positive long id) {
        return directorService.getDirectorById(id);
    }

    /**
     * Добавляет нового режиссера в базу данных.
     *
     * @param director объект режиссёра для добавления
     * @return созданный объект режиссёра
     */
    @PostMapping
    public Director createDirector(@Valid @RequestBody Director director) {
        return directorService.createDirector(director);
    }

    /**
     * Обновляет существующего режиссёра в базе данных.
     *
     * @param director обновляемый объект режиссёра
     * @return обновленный объект режиссёра
     */
    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director) {
        return directorService.updateDirector(director);
    }

    /**
     * Удаление режиссёра по идентификатору.
     *
     * @param id уникальный идентификатор фильма
     * @return пустой ответ с успешным статусом
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable @Positive long id) {
        directorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
