package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
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
        String sqlQuery = "SELECT distinct * FROM GENRE g join GENRE_TYPE gt on g.GENRE_ID = gt.GENRE_ID where FILM_ID = ?";
        return jdbcTemplate.query(sqlQuery, this::mapToGenre, id);
    }

    public List<Integer> getGenresIds() {
        return getGenresList().stream().map(Genre::getId).collect(Collectors.toList());
    }

    public Genre mapToGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("name"))
                .build();
    }
}
