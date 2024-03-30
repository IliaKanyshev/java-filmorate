package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.LikeStorage;
import ru.yandex.practicum.filmorate.storage.film.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikeDbStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaStorage mpaStorage;

    @Override
    public void like(Integer filmId, Integer userId) {
        jdbcTemplate.update("MERGE INTO LIKES (FILM_ID, USER_ID) VALUES(?, ?)", filmId, userId);
        log.info("Пользователь с id {} поставил лайк фильму с id {}", userId, filmId);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        jdbcTemplate.update("DELETE from LIKES where FILM_ID = ? and USER_ID = ?", filmId, userId);
        log.info("Пользователь с id {} удалил лайк фильму с id {}", userId, filmId);
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        String sqlQuery = "SELECT f.* from FILMS f " +
                "join MPA M on M.MPA_RATING_ID = f.MPA_RATING_ID" +
                " join LIKES l on f.FILM_ID = l.FILM_ID group by f.FILM_ID order by count(l.USER_ID) desc limit ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }

    @Override
    public Set<Integer> getLikesById(Integer id) {
        String sql = "SELECT USER_ID FROM LIKES WHERE FILM_ID = ?";
        Set<Integer> likes = new HashSet<>();
        SqlRowSet users = jdbcTemplate.queryForRowSet(sql, id);
        while (users.next()) {
            likes.add(users.getInt("USER_ID"));
        }
        return likes;
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(mpaStorage.getMpaById(resultSet.getInt("mpa_rating_id")))
                .build();
    }
}
