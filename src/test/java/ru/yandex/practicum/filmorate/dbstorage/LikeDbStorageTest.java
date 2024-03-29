package ru.yandex.practicum.filmorate.dbstorage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.LikeDbStorage;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class LikeDbStorageTest {
    private final LikeDbStorage likeDbStorage;
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private Film film;
    private User user;

    @BeforeEach
    public void init() {
        film = Film.builder()
                .id(1)
                .name("film")
                .description("desc")
                .duration(100)
                .releaseDate(LocalDate.of(2020, 12, 12))
                .mpa(new Mpa(1, "G"))
                .genres(List.of(new Genre(1, "Комедия")))
                .build();
        user = User.builder()
                .id(1)
                .email("vasya@mail.ru")
                .login("vasya")
                .name("vasek")
                .birthday(LocalDate.of(2020, 12, 12))
                .friends(new HashSet<>())
                .build();
    }

    @Test
    public void likeTest() {
        filmDbStorage.createFilm(film);
        userDbStorage.createUser(user);
        likeDbStorage.like(film.getId(), user.getId());
        assertEquals(filmDbStorage.getFilmById(film.getId()).getLikes().size(), 1);
    }

    @Test
    public void deleteLikeTest() {
        filmDbStorage.createFilm(film);
        userDbStorage.createUser(user);
        likeDbStorage.like(film.getId(), user.getId());
        assertEquals(filmDbStorage.getFilmById(film.getId()).getLikes().size(), 1);
        likeDbStorage.deleteLike(film.getId(), user.getId());
        assertEquals(filmDbStorage.getFilmById(film.getId()).getLikes().size(), 0);
    }

    @Test
    public void getPopularFilmsTest() {
        filmDbStorage.createFilm(film);
        userDbStorage.createUser(user);
        likeDbStorage.like(film.getId(), user.getId());
        assertEquals(likeDbStorage.getPopularFilms(10).size(), 1);
    }

    @Test
    public void getLikesById() {
        filmDbStorage.createFilm(film);
        userDbStorage.createUser(user);
        likeDbStorage.like(film.getId(), user.getId());
        assertEquals(likeDbStorage.getLikesById(film.getId()).size(), 1);
    }
}
