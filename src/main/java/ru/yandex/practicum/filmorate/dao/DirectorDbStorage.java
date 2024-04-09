package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.DirectorStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Director> directorRowMapper = ((rs, rowNum) -> Director.builder()
            .id(rs.getInt("director_id"))
            .name(rs.getString("name"))
            .build());

    @Override
    public List<Director> getAllDirectors() {
        log.info("Получение списка всех режиссеров");
        String sql = "SELECT * FROM directors";
        return jdbcTemplate.query(sql, directorRowMapper);
    }

    @Override
    public Optional<Director> getById(int id) {
        log.info("Получение режиссера с ID: {}", id);
        String sql = "SELECT * FROM directors WHERE director_id = ?";
        List<Director> director = jdbcTemplate.query(sql, directorRowMapper, id);
        if (director.isEmpty()) {
            log.warn("Режиссер с ID {} не найден", id);
            return Optional.empty();
        }
        return director.stream().findFirst();
    }

    @Override
    public Director createDirector(Director director) {
        log.info("Создание нового режиссера: {}", director);
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("directors")
                .usingGeneratedKeyColumns("director_id");
        Map<String, Object> values = new HashMap<>();
        values.put("name", director.getName());
        int id = simpleJdbcInsert.executeAndReturnKey(values).intValue();
        director.setId(id);
        log.info("Режиссер {} создан", director);
        return director;
    }

    @Override
    public Director update(Director director) {
        log.info("Обновление режиссера с ID {}", director.getId());
        String sql = "UPDATE directors SET name = ? where DIRECTOR_ID = ?";
        jdbcTemplate.update(sql, director.getName(), director.getId());
        return director;
    }

    @Override
    public void deleteById(int id) {
        log.info("Удаление режиссера с ID: {}", id);
        String sql = "DELETE FROM directors WHERE director_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Director> getDirectorsListById(Integer id) {
        log.info("Получение списка режиссеров по ID фильма: {}", id);
        String sqlQuery = "SELECT d.DIRECTOR_ID, d.NAME from DIRECTOR_FILM df" +
                " inner join DIRECTORS d on df.DIRECTOR_ID = d.DIRECTOR_ID where FILM_ID = ?";
        return jdbcTemplate.query(sqlQuery, directorRowMapper, id);
    }

    @Override
    public void updateFilmDirector(Film film) {
        log.info("Обновление режиссеров для фильма с ID: {}", film.getId());
        String sqlQuery = "DELETE from DIRECTOR_FILM where FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
        if (film.getDirectors() != null) {
            for (Director director : film.getDirectors()) {
                jdbcTemplate.update(
                        "MERGE INTO DIRECTOR_FILM (film_id, director_id) VALUES (?, ?);",
                        film.getId(),
                        director.getId()
                );
            }
        }
    }
}
