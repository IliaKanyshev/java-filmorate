package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmServiceTest {
    private FilmService filmService;
    private UserStorage userStorage;
    private Film film;
    private Film film2;
    private Film film3;
    private Film film4;
    private User user;
    private User user2;

    @BeforeEach
    public void init() {
        userStorage = new InMemoryUserStorage();
        filmService = new FilmService(new InMemoryFilmStorage(), userStorage);
        film = Film.builder()
                .id(1)
                .name("film")
                .description("filmDescr")
                .releaseDate(LocalDate.of(1990, 12, 12))
                .duration(100)
                .build();
        film2 = Film.builder()
                .id(2)
                .name("film2")
                .description("filmDescr2")
                .releaseDate(LocalDate.of(1991, 12, 12))
                .duration(100)
                .build();
        film3 = Film.builder()
                .id(3)
                .name(" ")
                .description("filmDescr3aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                .releaseDate(LocalDate.of(1852, 12, 12))
                .duration(0)
                .build();
        film4 = Film.builder()
                .id(4)
                .name("film4")
                .description("filmDescr4")
                .releaseDate(LocalDate.of(1992, 12, 12))
                .duration(100)
                .build();
        user = User.builder()
                .id(1)
                .email("vasya@mail.ru")
                .login("Vasya")
                .birthday(LocalDate.of(1990, 12, 12))
                .build();
        user2 = User.builder()
                .id(2)
                .email("vasya1@mail.ru")
                .login("Vasya1")
                .birthday(LocalDate.of(1991, 12, 12))
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
}
