package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;
import ru.yandex.practicum.filmorate.storage.film.LikeStorage;
import ru.yandex.practicum.filmorate.storage.film.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final LikeStorage likeStorage;

    @Override
    public List<Film> getFilms() {
        log.info("Список всех фильмов:");
        String sqlQuery = "SELECT * FROM FILMS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Film createFilm(Film film) {
        if (checkGenreExist(film)) {
            throw new ValidationException("Ошибка genre_id.");
        }
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("FILMS")
                .usingGeneratedKeyColumns("FILM_ID");
        int key = simpleJdbcInsert.executeAndReturnKey(filmToMap(film)).intValue();
        film.setId(key);
        checkFilmGenres(film);
        log.debug("Добавлен фильм {} с ID {}.", film.getName(), film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        getFilmById(film.getId());
        String sqlQuery = "UPDATE FILMS SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA_RATING_ID = ? WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        checkFilmGenres(film);
        log.info("Фильм с id {} обновлен.", film.getId());
        return getFilmById(film.getId());
    }

    @Override
    public void deleteFilmById(Integer id) {
        getFilmById(id);
        String sqlQuery = "DELETE FROM FILMS WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
        log.info("Фильм с id {} удален.", id);
    }

    @Override
    public Film getFilmById(Integer id) {
        String sqlQuery = "SELECT f.*, m.NAME FROM FILMS f left join MPA m on f.MPA_RATING_ID = m.MPA_RATING_ID WHERE FILM_ID = ?";
        try {
            Film film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
            log.info("Фильм с id {} найден.", id);
            return film;
        } catch (RuntimeException e) {
            log.info("Фильм с id {} не найден.", id);
            throw new NotFoundException(String.format("Фильм с id %d не найден.", id));
        }
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(mpaStorage.getMpaById(resultSet.getInt("mpa_rating_id")))
                .likes(likeStorage.getLikesById(resultSet.getInt("film_id")))
                .genres(genreStorage.getGenreListById(resultSet.getInt("film_id")))
                .build();
    }

    public Map<String, Object> filmToMap(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("mpa_rating_id", film.getMpa().getId());
        values.put("genres", film.getGenres());
        return values;
    }

    private void checkFilmGenres(Film film) {
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(
                        "INSERT INTO GENRE (film_id, genre_id) VALUES (?, ?);",
                        film.getId(),
                        genre.getId()
                );
            }
        }
    }

    private boolean checkGenreExist(Film film) {
        if (film.getGenres() == null) {
            return false;
        }
        return film.getGenres().stream().anyMatch(genre -> genre.getId() > 6);
    }
}
