package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Mpa getMpaById(Integer id) {
        try {
            String sqlQuery = "SELECT * FROM MPA WHERE MPA_RATING_ID = ?";
            return jdbcTemplate.queryForObject(sqlQuery, this::mapToMpa, id);
        } catch (RuntimeException e) {
            log.info("Неверный id mpa.");
            throw new NotFoundException("Неверный id mpa.");
        }
    }

    @Override
    public List<Mpa> getMpaList() {
        String sqlQuery = "SELECT * FROM MPA order by MPA_RATING_ID";
        return jdbcTemplate.query(sqlQuery, this::mapToMpa);
    }

    public Mpa mapToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("mpa_rating_id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
