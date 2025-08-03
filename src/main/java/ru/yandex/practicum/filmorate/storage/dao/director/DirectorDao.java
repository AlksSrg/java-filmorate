package ru.yandex.practicum.filmorate.storage.dao.director;

import java.util.List;
import ru.yandex.practicum.filmorate.model.Director;

public interface DirectorDao {

    /**
     * Получение списка режиссеров фильма
     *
     * @param filmId идентификатор фильма
     * @return список режиссеров фильма
     */
    List<Director> getFilmDirectors(long filmId);

}
