package ru.yandex.practicum.filmorate.dbstorage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class GenreDbStorageTest {
    private final GenreDbStorage genreDbStorage;
    private final FilmDbStorage filmDbStorage;
    private Film film;

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
    }

    @Test
    public void getGenreByIdTest() {
        filmDbStorage.createFilm(film);
        genreDbStorage.updateFilmGenres(film);
        List<Genre> genres = genreDbStorage.getGenreListById(film.getId());
        List<Genre> genres1 = film.getGenres();
        assertThat(genres)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(genres1);
    }

    @Test
    public void getGenreListTest() {
        assertEquals(genreDbStorage.getGenresList().size(), 6);
    }

    @Test
    public void getGenreListByIdTest() {
        filmDbStorage.createFilm(film);
        genreDbStorage.updateFilmGenres(film);
        assertEquals(genreDbStorage.getGenreListById(film.getId()).size(), 1);
    }
}
