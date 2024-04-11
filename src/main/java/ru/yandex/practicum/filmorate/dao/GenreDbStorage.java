package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre getGenreById(Integer id) {
        try {
            String sqlQuery = "select * from GENRE_TYPE where GENRE_ID = ?";
            return jdbcTemplate.queryForObject(sqlQuery, this::mapToGenre, id);
        } catch (RuntimeException e) {
            log.info("Неверный id genre.");
            throw new NotFoundException("Неверный id genre.");
        }
    }

    @Override
    public List<Genre> getGenresList() {
        String sqlQuery = "select * from GENRE_TYPE order by GENRE_ID";
        return jdbcTemplate.query(sqlQuery, this::mapToGenre);
    }

    @Override
    public List<Genre> getGenreListById(Integer id) {
        String sqlQuery =
                "SELECT distinct gt.genre_id, gt.name FROM genre_type gt " +
                        "INNER JOIN genre g ON gt.genre_id = g.genre_id " +
                        "WHERE g.film_id = ?";
        return jdbcTemplate.query(sqlQuery, this::mapToGenre, id);
    }


    @Override
    public Map<Integer, List<Genre>> getFilmIdGenresMap() {
        String sqlQuery =
                "SELECT g.FILM_ID, gt.genre_id, gt.name " +
                        "FROM GENRE g " +
                        "JOIN GENRE_TYPE gt ON g.genre_id = gt.genre_id ";

        SqlRowSet filmGenreSet = jdbcTemplate.queryForRowSet(sqlQuery);

        HashMap<Integer, List<Genre>> filmGenresMap = new HashMap<>();

        while (filmGenreSet.next()) {
            Integer filmId = filmGenreSet.getInt("FILM_ID");

            Genre genre = Genre.builder()
                    .id(filmGenreSet.getInt("genre_id"))
                    .name(filmGenreSet.getString("name"))
                    .build();
            filmGenresMap.computeIfAbsent(filmId, flmId -> new ArrayList<>()).add(genre);
        }
        log.info("Словарь жанров для фильмов сформирован: \n {}", filmGenresMap);
        return filmGenresMap;
    }


    public List<Integer> getGenresIds() {
        return getGenresList().stream().map(Genre::getId).collect(Collectors.toList());
    }

    @Override
    public void updateFilmGenres(Film film) {
        String sqlQuery = "DELETE from GENRE where FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(
                        "MERGE INTO GENRE (film_id, genre_id) VALUES (?, ?);",
                        film.getId(),
                        genre.getId()
                );
            }
        }
    }

    public Genre mapToGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("name"))
                .build();
    }
}
