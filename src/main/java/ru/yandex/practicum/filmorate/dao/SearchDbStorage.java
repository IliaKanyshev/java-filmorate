package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmSearch;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class SearchDbStorage implements FilmSearch {
    private final JdbcTemplate jdbcTemplate;
    private final FilmMapper filmMapper;

    @Override
    public List<Film> getFilmListByTitle(String query) {
        String sqlQuery = "SELECT f.*, m.NAME FROM FILMS f LEFT JOIN MPA M on M.MPA_RATING_ID = f.MPA_RATING_ID " +
                "LEFT JOIN LIKES L on f.FILM_ID = L.FILM_ID WHERE f.NAME ilike CONCAT('%', ?, '%') " +
                "GROUP BY f.FILM_ID " +
                "ORDER BY COUNT(l.USER_ID) DESC";
        return jdbcTemplate.query(sqlQuery, filmMapper, query);
    }

    @Override
    public List<Film> getFilmListByDirector(String query) {
        String sqlQuery =
                "SELECT f.*, m.NAME FROM FILMS f " +
                        "LEFT JOIN MPA M on M.MPA_RATING_ID = f.MPA_RATING_ID " +
                        "LEFT JOIN DIRECTOR_FILM DF on f.FILM_ID = DF.FILM_ID " +
                        "LEFT JOIN DIRECTORS D on D.DIRECTOR_ID = DF.DIRECTOR_ID " +
                        "LEFT JOIN LIKES L on f.FILM_ID = L.FILM_ID WHERE d.NAME ilike concat('%', ?, '%') " +
                        "GROUP BY f.FILM_ID " +
                        "ORDER BY COUNT(l.USER_ID) DESC";
        return jdbcTemplate.query(sqlQuery, filmMapper, query);
    }

    @Override
    public List<Film> getFilmListByTitleAndDirector(String query) {
        String sqlQuery =
                "SELECT f.*, m.NAME FROM FILMS f " +
                        "LEFT JOIN MPA M on M.MPA_RATING_ID = f.MPA_RATING_ID " +
                        "LEFT JOIN DIRECTOR_FILM DF on f.FILM_ID = DF.FILM_ID " +
                        "LEFT JOIN DIRECTORS D on D.DIRECTOR_ID = DF.DIRECTOR_ID " +
                        "LEFT JOIN LIKES L on f.FILM_ID = L.FILM_ID " +
                        "WHERE instr(lower(coalesce(f.NAME,' ') || coalesce(d.NAME,' ')),lower(?))>0 " +
                        "GROUP BY f.FILM_ID " +
                        "ORDER BY COUNT(l.USER_ID) DESC";
        return jdbcTemplate.query(sqlQuery, filmMapper, query);
    }
}
