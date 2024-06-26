package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmServiceTest {
    private final FilmService filmService;
    private final UserStorage userStorage;
    private final DirectorStorage directorStorage;
    private Film film;
    private Film film2;
    private Film film3;
    private Film film4;
    private User user;
    private User user2;

    @BeforeEach
    public void init() {
        film = Film.builder()
                .id(1)
                .name("film")
                .description("desc")
                .duration(100)
                .releaseDate(LocalDate.of(2020, 12, 12))
                .mpa(new Mpa(1, "G"))
                //.(List.of(new Genre(1, "Комедия")))
                //   .likes(new HashSet<>())
                .build();
        film.getGenres().add((new Genre(1, "Комедия")));
        film2 = Film.builder()
                .id(2)
                .name("updated")
                .description("filmDescr2")
                .releaseDate(LocalDate.of(1991, 12, 12))
                .duration(100)
                .mpa(new Mpa(1, "G"))
                // .genres(List.of(new Genre(1, "Комедия")))
                //  .likes(new HashSet<>())
                .build();
        film3 = Film.builder()
                .id(3)
                .name(" ")
                .description("filmDescr3aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                .releaseDate(LocalDate.of(1852, 12, 12))
                .duration(0)
                .mpa(new Mpa(1, "G"))
                //    .genres(List.of(new Genre(1, "Комедия")))
                //   .likes(new HashSet<>())
                .build();
        film4 = Film.builder()
                .id(4)
                .name("dated")
                .description("filmDescr4")
                .releaseDate(LocalDate.of(1992, 12, 12))
                .duration(100)
                .mpa(new Mpa(1, "G"))
                //    .genres(List.of(new Genre(1, "Комедия")))
                //    .likes(new HashSet<>())
                .build();
        user = User.builder()
                .id(1)
                .email("vasya@mail.ru")
                .name("Vas")
                .login("Vasya")
                .birthday(LocalDate.of(1990, 12, 12))
                .friends(new HashSet<>())
                .build();
        user2 = User.builder()
                .id(2)
                .email("vasya1@mail.ru")
                .name("Vas1")
                .login("Vasya1")
                .birthday(LocalDate.of(1991, 12, 12))
                .friends(new HashSet<>())
                .build();
    }

    @Test
    public void createFilmTest() {
        filmService.createFilm(film);
        assertEquals(filmService.getFilms().size(), 1);
    }

    @Test
    public void updateFilmTest() {
        filmService.createFilm(film);
        film = film.toBuilder().name("newFilm").build();
        filmService.updateFilm(film);
        assertEquals(filmService.getFilm(1).getName(), "newFilm");
    }

    @Test
    public void deleteFilmTest() {
        filmService.createFilm(film);
        assertEquals(filmService.getFilms().size(), 1);
        filmService.deleteFilm(1);
        assertEquals(filmService.getFilms().size(), 0);
    }

    @Test
    public void getFilmsTest() {
        filmService.createFilm(film);
        filmService.createFilm(film2);
        assertEquals(filmService.getFilms().size(), 2);
    }

    @Test
    public void getFilmTest() {
        filmService.createFilm(film);
        filmService.createFilm(film2);
        assertEquals(filmService.getFilm(1), film);
        assertEquals(filmService.getFilm(2), film2);
    }

    @Test
    public void likeTest() {
        filmService.createFilm(film);
        userStorage.createUser(user);
        filmService.like(1, 1);
        assertEquals(filmService.getFilm(1).getLikes().size(), 1);
    }

    @Test
    public void deleteLikeTest() {
        filmService.createFilm(film);
        userStorage.createUser(user);
        filmService.like(1, 1);
        assertEquals(filmService.getFilm(1).getLikes().size(), 1);
        filmService.deleteLike(1, 1);
        assertEquals(filmService.getFilm(1).getLikes().size(), 0);
    }

    @Test
    public void getPopularFilmsTest() {
        filmService.createFilm(film);
        userStorage.createUser(user);
        filmService.like(1, 1);
        assertEquals(filmService.getPopularFilms(10).size(), 1);
        filmService.createFilm(film2);
        filmService.like(2, 1);
        assertEquals(filmService.getPopularFilms(10).size(), 2);
    }

    @Test
    public void getFilmsByTitleOrDirectorTest() {
        Director director = Director.builder()
                .id(1)
                .name("director")
                .build();
        Director director2 = Director.builder()
                .id(2)
                .name("Vasya")
                .build();
        Director director3 = Director.builder()
                .id(3)
                .name("rector")
                .build();
        filmService.createFilm(film);
        filmService.createFilm(film2);
        filmService.createFilm(film4);
        directorStorage.createDirector(director);
        directorStorage.createDirector(director2);
        directorStorage.createDirector(director3);
        film.setDirectors(List.of(director));
        film2.setDirectors(List.of(director2));
        film4.setDirectors(List.of(director3));
        filmService.updateFilm(film);
        filmService.updateFilm(film2);
        filmService.updateFilm(film4);
        assertEquals(2, filmService.getFilmsByTitleOrDirector("rector", List.of("director")).size());
        assertEquals(1, filmService.getFilmsByTitleOrDirector("vas", List.of("director")).size());
        assertEquals(2, filmService.getFilmsByTitleOrDirector("dated", List.of("title")).size());
    }
}
