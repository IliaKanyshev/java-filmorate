package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.LikeStorage;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikeDbStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmMapper filmMapper;

    @Override
    public void like(Integer filmId, Integer userId) {
        String sqlAddLike = "UPDATE films SET rate = rate + 1 WHERE film_id = ?";
        jdbcTemplate.update(sqlAddLike, filmId);
        jdbcTemplate.update("MERGE INTO LIKES (FILM_ID, USER_ID) VALUES(?, ?)", filmId, userId);
        log.info("Пользователь с id {} поставил лайк фильму с id {}", userId, filmId);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        String sqlAddLike = "UPDATE films SET rate = rate - 1 WHERE film_id = ?";
        jdbcTemplate.update(sqlAddLike, filmId);
        jdbcTemplate.update("DELETE from LIKES where FILM_ID = ? and USER_ID = ?", filmId, userId);
        log.info("Пользователь с id {} удалил лайк фильму с id {}", userId, filmId);
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        String sqlQuery = "SELECT f.*,m.NAME as MPA_name from FILMS f " +
                "join MPA M on M.MPA_RATING_ID = f.MPA_RATING_ID" +
                " LEFT JOIN LIKES l on f.FILM_ID = l.FILM_ID group by f.FILM_ID order by count(l.USER_ID) desc limit ?";
        return jdbcTemplate.query(sqlQuery, filmMapper, count);
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

    @Override
    public Map<Integer, Set<Integer>> getFilmLikesMap() {
        String sqlQuery = "SELECT * FROM LIKES ";
        SqlRowSet filmLikeSet = jdbcTemplate.queryForRowSet(sqlQuery);

        HashMap<Integer, Set<Integer>> filmLikeMap = new HashMap<>();

        while (filmLikeSet.next()) {
            Integer filmId = filmLikeSet.getInt("FILM_ID");
            Integer userId = filmLikeSet.getInt("USER_ID");
            filmLikeMap.computeIfAbsent(filmId, flmId -> new HashSet<>()).add(userId);
        }
        log.info("Множество соответствующих лайков для всех фильмов сформировано: \n {}" , filmLikeMap);
        return filmLikeMap;
    }
}
