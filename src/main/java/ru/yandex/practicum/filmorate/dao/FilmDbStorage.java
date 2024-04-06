package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmMapper filmMapper;

    @Override
    public List<Film> getFilms() {
        log.info("Список всех фильмов:");
        String sqlQuery = "SELECT f.*, m.NAME FROM FILMS f left join MPA m on f.MPA_RATING_ID = m.MPA_RATING_ID";
        return jdbcTemplate.query(sqlQuery, filmMapper);
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
        log.debug("Добавлен фильм {} с ID {}.", film.getName(), film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        getFilmById(film.getId());
        String sqlQuery = "UPDATE FILMS SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, mpa_rating_id = ? WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());
        log.info("Фильм с id {} обновлен.", film.getId());
        return getFilmById(film.getId());
    }

    @Override
    public void deleteFilmById(Integer id) {
        String sqlQuery = "DELETE FROM FILMS WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
        log.info("Фильм с id {} удален.", id);
    }

    @Override
    public Film getFilmById(Integer id) {
        String sqlQuery = "SELECT f.*, m.NAME FROM FILMS f left join MPA m on f.MPA_RATING_ID = m.MPA_RATING_ID WHERE FILM_ID = ?";
        try {
            Film film = jdbcTemplate.queryForObject(sqlQuery, filmMapper, id);
            log.info("Фильм с id {} найден.", id);
            return film;
        } catch (RuntimeException e) {
            log.info("Фильм с id {} не найден.", id);
            throw new NotFoundException(String.format("Фильм с id %d не найден.", id));
        }
    }

    public List<Film> getSortedFilms(int id, String sort) {
        String sqlQueryLikes = "SELECT f.*,MPA.NAME "
                + "FROM DIRECTOR_FILM df "
                + "LEFT JOIN films f ON f.FILM_ID = df.FILM_ID "
                + "LEFT JOIN likes l ON f.FILM_ID = l.FILM_ID "
                + "LEFT JOIN MPA MPA on MPA.MPA_RATING_ID = f.MPA_RATING_ID "
                + "WHERE df.DIRECTOR_ID = ?"
                + "GROUP BY f.FILM_ID "
                + "ORDER BY COUNT(l.USER_ID) DESC";
        String sqlQueryYears = "SELECT f.*, MPA.NAME "
                + "FROM DIRECTOR_FILM df "
                + "LEFT JOIN films f ON f.FILM_ID = df.FILM_ID "
                + "LEFT JOIN MPA MPA on MPA.MPA_RATING_ID = f.MPA_RATING_ID "
                + "WHERE df.DIRECTOR_ID = ?"
                + "ORDER BY f.RELEASE_DATE";
        List<Film> films;
        if (sort.equals("likes")) {
            films = jdbcTemplate.query(sqlQueryLikes, filmMapper, id);
        } else if (sort.equals("year")) {
            films = jdbcTemplate.query(sqlQueryYears, filmMapper, id);
        } else {
            throw new NotFoundException("Некорректный запрос");
        }
        log.info("Список отсортированных фильмов:");
        return films;
    }

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        log.info("Получение общих фильмов у пользователей с ID {} и {}", userId, friendId);
        String sql = "SELECT f.*, mpa.name FROM films f " +
                "JOIN likes l1 ON f.film_id = l1.film_id AND l1.user_id = ? " +
                "JOIN likes l2 ON f.film_id = l2.film_id AND l2.user_id = ? " +
                "LEFT JOIN mpa mpa ON mpa.mpa_rating_id = f.mpa_rating_id";
        return jdbcTemplate.query(sql, filmMapper, userId, friendId);
    }

    public Map<String, Object> filmToMap(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("mpa_rating_id", film.getMpa().getId());
        values.put("genres", film.getGenres());
        values.put("directors", film.getDirectors());
        return values;
    }

    private boolean checkGenreExist(Film film) {
        if (film.getGenres() == null) {
            return false;
        }
        return film.getGenres().stream().anyMatch(genre -> genre.getId() > 6);
    }

    @Override
    public List<Film> getPopularFilmsByGenreAndYear(Integer count, Integer genreId, Integer year) {
        String sqlQuery =
                "SELECT F.*, M.mpa_rating_id AS mpa_id, M.name AS mpa_name " +
                        "FROM films F " +
                        "LEFT JOIN likes L ON F.film_id = L.film_id " +
                        "LEFT JOIN mpa AS M ON F.mpa_rating_id = M.mpa_rating_id " +
                        "LEFT JOIN genre G on F.film_id = G.film_id %s" +
                        "GROUP BY F.name, F.film_id ORDER BY COUNT(l.film_id) DESC LIMIT ?";

        final List<String> params = new ArrayList<>();
        if (genreId != 0) {
            params.add(String.format("genre_id = %s", genreId));
        }
        if (year != 0) {
            params.add(String.format("YEAR(release_date) = %s", year));
        }
        final String genreAndYearParams = !params.isEmpty() ? "where ".concat(String.join(" and ", params)) : "";
        return jdbcTemplate.query(String.format(sqlQuery, genreAndYearParams), filmMapper, count);
    }
}

