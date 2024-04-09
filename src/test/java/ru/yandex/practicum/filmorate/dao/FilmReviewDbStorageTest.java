package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import ru.yandex.practicum.filmorate.dao.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.dao.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FilmReviewDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private FilmReviewDbStorage filmReviewDbStorage;
    private UserDbStorage userDbStorage;
    private FilmDbStorage filmDbStorage;

    @BeforeAll
    void init() {
        filmReviewDbStorage = new FilmReviewDbStorage(this.jdbcTemplate);
        userDbStorage = new UserDbStorage(this.jdbcTemplate, new UserMapper(this.jdbcTemplate));
        filmDbStorage = new FilmDbStorage(this.jdbcTemplate, new FilmMapper());

        User newUser = User.builder()
                .login("vanya123")
                .name("Ivan Petrov")
                .email("user@email.ru")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User savedUser = userDbStorage.createUser(newUser);
        final Film testFilm1 = Film.builder()
                .name("Matrix")
                .description("Good film!")
                .duration(120)
                .mpa(new Mpa(1, "G"))
                .releaseDate(LocalDate.of(2000, 1, 1))
                .build();

        Film savedFilm1 = filmDbStorage.createFilm(testFilm1);
    }

    @BeforeEach
    void setUp() {
        JdbcTestUtils.deleteFromTables(this.jdbcTemplate, "FILM_REVIEW");
    }

    @Test
    void getAllReviews() {
    }

    @Test
    void saveReview() {
        Review review = Review.builder()
                .content("Test review")
                .isPositive(Boolean.TRUE)
                .userId(1)
                .filmId(1)
                .build();
        Review reviewSaved = filmReviewDbStorage.saveReview(review);
        assertEquals(1, review.getReviewId());

        assertThat(review)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(reviewSaved);

        assertEquals(0, review.getUseful());
    }

    @Test
    void updateReview() {
    }

    @Test
    void deleteReviewById() {
    }

    @Test
    void getReviewById() {
    }

    @Test
    void getAllReviewsLimited() {
    }

    @Test
    void getAllFilmReviewsLimited() {
    }

    @Test
    void setReaction() {
        Review review = Review.builder()
                .content("Test review")
                .isPositive(Boolean.TRUE)
                .userId(1)
                .filmId(1)
                .useful(10)
                .build();
        Integer savedReviewId = filmReviewDbStorage.saveReview(review).getReviewId();
        assertEquals(2, filmReviewDbStorage.getReviewById(savedReviewId).get().getReviewId());

        filmReviewDbStorage.setReaction(savedReviewId, 1, -1);
        assertEquals(9, filmReviewDbStorage.getReviewById(savedReviewId).get().getUseful());

        filmReviewDbStorage.setReaction(savedReviewId, 1, -1);
        assertEquals(9, filmReviewDbStorage.getReviewById(savedReviewId).get().getUseful());

        filmReviewDbStorage.setReaction(savedReviewId, 1, -1);
        assertEquals(9, filmReviewDbStorage.getReviewById(savedReviewId).get().getUseful());
    }

    @Test
    void deleteUserReaction() {
    }
}