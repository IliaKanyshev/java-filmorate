package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
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
            .useful(rs.getInt("USEFUL_VALUE"))
            .build();

    private final String SQL_GET_ALL_REVIEWS =
            "select fr.ID, fr.CONTENT, fr.IS_POSITIVE, fr.USER_ID, fr.FILM_ID,\n" +
                    "    sum(coalesce(rr.REACTION_VALUE, 0)) as USEFUL_VALUE\n" +
                    "     from FILM_REVIEW fr\n" +
                    "     left join REVIEW_REACTION RR on fr.ID = RR.REVIEW_ID\n" +
                    "    group by fr.id, fr.CONTENT, fr.IS_POSITIVE, fr.USER_ID, fr.FILM_ID\n" +
                    "    order by sum(coalesce(rr.REACTION_VALUE, 0)) desc";

    @Override
    public List<Review> getAllReviews() {
        return jdbcTemplate.query(SQL_GET_ALL_REVIEWS, reviewRowMapper);
    }

    @Override
    public Review saveReview(Review review) {
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
        return parameters;
    }

    @Override
    public Optional<Review> updateReview(Review review) {
        int updated = jdbcTemplate.update(
                "UPDATE FILM_REVIEW set CONTENT =?, IS_POSITIVE =?, USER_ID=?, FILM_ID = ? where ID = ?",
                review.getContent(), review.getIsPositive(), review.getUserId(), review.getFilmId(),review.getReviewId());
        return updated == 1 ? Optional.of(review) : Optional.empty();
    }

    @Override
    public void deleteReviewById(Integer id) {
        String deleteReviewQuery = "DELETE FROM FILM_REVIEW WHERE ID = ?";
        jdbcTemplate.update(deleteReviewQuery, id);
        log.info("Отзыв с id {} удален.", id);
    }

    @Override
    public Optional<Review> getReviewById(Integer reviewId) {
        final String SQL_GET_REVIEW =
                "select fr.*, \n" +
                        "(select coalesce(sum(rr.REACTION_VALUE), 0)\n" +
                        "  from REVIEW_REACTION rr\n" +
                        " where rr.REVIEW_ID = fr.id) as USEFUL_VALUE\n" +
                        "  from FILM_REVIEW fr\n" +
                        " where fr.id = ?";
        Review review = jdbcTemplate.queryForObject(SQL_GET_REVIEW, reviewRowMapper, reviewId);
        return Optional.ofNullable(review);
    }

    @Override
    public List<Review> getAllReviewsLimited(Integer count) {
        return jdbcTemplate.query(SQL_GET_ALL_REVIEWS + " LIMIT " + count.toString(), reviewRowMapper);
    }

    @Override
    public List<Review> getAllFilmReviewsLimited(Integer filmId, Integer count) {
        String SQL_GET_ALL_FILM_REVIEWS =
                "select fr.id, fr.CONTENT, fr.IS_POSITIVE, fr.USER_ID, fr.FILM_ID,\n" +
                        " sum(coalesce(rr.REACTION_VALUE, 0)) as USEFUL_VALUE\n" +
                        "from FILM_REVIEW fr\n" +
                        "left join REVIEW_REACTION RR on fr.ID = RR.REVIEW_ID\n" +
                        " where  fr.FILM_ID = ?\n" +
                        "group by fr.id, fr.CONTENT, fr.IS_POSITIVE, fr.USER_ID, fr.FILM_ID\n" +
                        "order by sum(coalesce(rr.REACTION_VALUE, 0)) desc";
        return jdbcTemplate.query(SQL_GET_ALL_FILM_REVIEWS + " LIMIT " + count.toString(), reviewRowMapper, filmId);
    }

    @Override
    public Optional<Review> setReaction(Integer reviewId, Integer userId, int i) {
        jdbcTemplate.update(
                "MERGE INTO REVIEW_REACTION (REVIEW_ID, USER_ID, REACTION_VALUE) VALUES(?, ?, ?)",
                reviewId,
                userId);
        log.info("Добавлена реакция пользователя с user_id={} на отзыв review_id={}", userId, reviewId);
        return getReviewById(reviewId);
    }

    @Override
    public Optional<Review> deleteUserReaction(Integer reviewId, Integer userId) {
        String deleteReviewQuery = "DELETE FROM REVIEW_REACTION WHERE REVIEW_ID = ? and USER_ID = ?";
        jdbcTemplate.update(deleteReviewQuery, reviewId, userId);
        log.info("Реакция пользователя с userId={} на отнзыв с id={} удалена.", userId, reviewId);
        return getReviewById(reviewId);
    }
}

