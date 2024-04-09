package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmReviewStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class FilmReviewDbStorage implements FilmReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Review> reviewRowMapper = (rs, rowNum) -> Review.builder()
            .reviewId(rs.getInt("ID"))
            .content(rs.getString("CONTENT"))
            .isPositive(rs.getBoolean("IS_POSITIVE"))
            .userId(rs.getInt("USER_ID"))
            .filmId(rs.getInt("FILM_ID"))
            .useful(rs.getInt("USEFUL"))
            .build();

    private static final String SQL_GET_ALL_REVIEWS = "select * from FILM_REVIEW order by USEFUL desc";

    @Override
    public List<Review> getAllReviews() {
        return jdbcTemplate.query(SQL_GET_ALL_REVIEWS, reviewRowMapper);
    }

    @Override
    public Review saveReview(Review review) {
        String sql = "select count(*) from FILM_REVIEW where FILM_ID = ? and USER_ID = ?";
        Integer reviewCount = jdbcTemplate.queryForObject(sql, Integer.class, review.getFilmId(), review.getUserId());
        if (reviewCount == null) {
            return null;
        }
        if (reviewCount > 0) {
            LogDbStorage logDbStorage = new LogDbStorage(this.jdbcTemplate);
            logDbStorage.saveLog(review.getUserId(), review.getReviewId(), "REVIEW", "ADD");
            throw new ValidationException("Отзыв уже существует");
        }

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILM_REVIEW")
                .usingGeneratedKeyColumns("ID");
        Map<String, Object> parameters = reviewToMap(review);
        Integer id = (Integer) simpleJdbcInsert.executeAndReturnKey(parameters);
        review.setReviewId(id);
        return review;
    }

    private Map<String, Object> reviewToMap(Review review) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("CONTENT", review.getContent());
        parameters.put("IS_POSITIVE", review.getIsPositive());
        parameters.put("USER_ID", review.getUserId());
        parameters.put("FILM_ID", review.getFilmId());
        parameters.put("USEFUL", review.getUseful());
        return parameters;
    }

    @Override
    public Optional<Review> updateReview(Review review) {
        int updated = jdbcTemplate.update(
                "UPDATE FILM_REVIEW set CONTENT =?, IS_POSITIVE =? where ID = ?",
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());
        Review updatedReview = getReviewById(review.getReviewId()).get();
        return updated == 1 ? Optional.of(updatedReview) : Optional.empty();
    }

    @Override
    public void deleteReviewById(Integer id) {
        String deleteReviewQuery = "DELETE FROM FILM_REVIEW WHERE ID = ?";
        jdbcTemplate.update(deleteReviewQuery, id);
        log.info("Отзыв с id {} удален.", id);
    }

    @Override
    public Optional<Review> getReviewById(Integer reviewId) {
        final String SQL_GET_REVIEW = "select * from FILM_REVIEW where id = ?";
        try {
            Review review = jdbcTemplate.queryForObject(SQL_GET_REVIEW, reviewRowMapper, reviewId);
            return Optional.ofNullable(review);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Review> getAllReviewsLimited(Integer count) {
        return jdbcTemplate.query(SQL_GET_ALL_REVIEWS + " LIMIT " + count.toString(), reviewRowMapper);
    }

    @Override
    public List<Review> getAllFilmReviewsLimited(Integer filmId, Integer count) {
        String sqlQuery = "select * from FILM_REVIEW fr where fr.FILM_ID = ? order by USEFUL desc LIMIT ?";
        return jdbcTemplate.query(sqlQuery, reviewRowMapper, filmId, count);
    }

    @Override
    public Optional<Review> setReaction(Integer reviewId, Integer userId, int reactionValue) {
        String sql = "select count(*) from REVIEW_REACTION where REVIEW_ID = ? and USER_ID = ? and REACTION_VALUE = ?";
        Integer reactionCount = jdbcTemplate.queryForObject(sql, Integer.class, reviewId, userId, reactionValue);
        if (reactionCount == null) {
            return Optional.empty();
        }
        if (reactionCount == 0) {
            jdbcTemplate.update(
                    "UPDATE FILM_REVIEW set USEFUL = USEFUL + (?) where ID = ?",
                    reactionValue, reviewId);
            log.info("Добавлена реакция пользователя с user_id={} на отзыв review_id={}", userId, reviewId);
        }

        jdbcTemplate.update(
                "MERGE INTO REVIEW_REACTION (REVIEW_ID, USER_ID, REACTION_VALUE) VALUES(?, ?, ?)",
                reviewId,
                userId,
                reactionValue);
        return getReviewById(reviewId);
    }

    @Override
    public Optional<Review> deleteUserReaction(Integer reviewId, Integer userId) {
        String sql = "select REACTION_VALUE from REVIEW_REACTION where REVIEW_ID = ? and USER_ID = ?";
        Integer reactionValue = jdbcTemplate.queryForObject(sql, Integer.class, reviewId, userId);

        jdbcTemplate.update(
                "UPDATE FILM_REVIEW set USEFUL = USEFUL - (?) where ID = ?",
                reactionValue, reviewId);
        String deleteReviewQuery = "DELETE FROM REVIEW_REACTION WHERE REVIEW_ID = ? and USER_ID = ?";
        jdbcTemplate.update(deleteReviewQuery, reviewId, userId);
        log.info("Реакция пользователя с userId={} на отнзыв с id={} удалена.", userId, reviewId);
        return getReviewById(reviewId);
    }
}

