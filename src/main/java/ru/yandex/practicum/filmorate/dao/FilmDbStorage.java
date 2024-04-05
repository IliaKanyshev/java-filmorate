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
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

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
                 "select f.*, m.mpa_rating_id as mpa_id, m.name as mpa_name " +
                "from films f "+
                "left join likes l on f.film_id = l.film_id " +
                "LEFT JOIN mpa AS m ON f.mpa_rating_id = m.mpa_rating_id " +
                "left join genre g on f.film_id = g.film_id %s"+
                "group by f.name, f.film_id order by count(l.film_id) desc limit ?";

        final List<String> params = new ArrayList<>();
        if (genreId!=0) {
            params.add(String.format("genre_id = %s", genreId));
        }
        if (year!=0) {
            params.add(String.format("YEAR(release_date) = %s", year));
        }
        final String genreAndYearParams = !params.isEmpty() ? "where ".concat(String.join(" and ", params)) : "";
        List<Film> films = jdbcTemplate.query(String.format(sqlQuery, genreAndYearParams), filmMapper, count);
        return setFilmGenres(films);
    }
    private List<Film> setFilmGenres(List<Film> films) {
        Map<Integer, List<Genre>> filmGenresMap = getAllFilmGenres(films);
        films.forEach(film -> {
            Integer filmId = film.getId();
            film.setGenres(filmGenresMap.getOrDefault(filmId, new ArrayList<>()));
        });
        return films;
    }
    public Map<Integer, List<Genre>> getAllFilmGenres(List<Film> films) {
        final String sql = "select g.film_id as film_id, gt.genre_id as genre_id, gt.name as name from genre g " +
                "left join genre_type gt on g.genre_id = gt.genre_id where g.film_id in (%s)";
        Map<Integer, List<Genre>> filmGenresMap = new HashMap<>();
        List<String> ids = films.stream().map(film -> String.valueOf(film.getId())).collect(Collectors.toList());
        jdbcTemplate.query(String.format(sql, String.join(",", ids)), rs -> {
            Genre genre = new Genre(rs.getInt("genre_id"), rs.getString("name"));
            Integer filmId = rs.getInt("film_id");
            filmGenresMap.putIfAbsent(filmId, new ArrayList<>());
            filmGenresMap.get(filmId).add(genre);
        });
        return filmGenresMap;
    }



}

