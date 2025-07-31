package ru.yandex.practicum.filmorate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.dto.dtoclasses.GenreWithIdAndName;
import ru.yandex.practicum.filmorate.dto.dtoclasses.MpaWithIdAndName;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

@Configuration
public class JdbcConfig {

    @Bean
    public RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            user.setName(rs.getString("name"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());
            return user;
        };
    }

    @Bean
    public RowMapper<Film> filmRowMapper() {
        return (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getLong("id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            return film;
        };
    }

    @Bean
    public RowMapper<GenreWithIdAndName> genreRowMapper() {
        return (rs, rowNum) -> {
            GenreWithIdAndName genre = new GenreWithIdAndName();
            genre.setId(rs.getInt("id"));
            genre.setName(rs.getString("name"));

            return genre;
        };
    }

    @Bean
    public RowMapper<MpaWithIdAndName> mpaWithIdAndNameRowMapper() {
        return (rs, rowNum) -> {
            MpaWithIdAndName genre = new MpaWithIdAndName();
            genre.setId(rs.getInt("id"));
            genre.setName(rs.getString("name"));

            return genre;
        };
    }
}
