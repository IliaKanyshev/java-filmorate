package ru.yandex.practicum.filmorate.dbstorage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.DirectorDbStorage;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;
    private final DirectorDbStorage directorDbStorage;
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
                .build();
    }

    @Test
    public void getFilmByIdTest() {
        filmDbStorage.createFilm(film);
        Film film1 = filmDbStorage.getFilmById(film.getId());
        assertThat(film1)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film);
    }

    @Test
    public void getFilmsTest() {
        filmDbStorage.createFilm(film);
        assertEquals(filmDbStorage.getFilms().size(), 1);
    }

    @Test
    public void createFilmTest() {
        filmDbStorage.createFilm(film);
        Film film1 = filmDbStorage.getFilmById(film.getId());
        assertThat(film1)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film);
        assertEquals(filmDbStorage.getFilms().size(), 1);
    }

    @Test
    public void updateFilmTest() {
        filmDbStorage.createFilm(film);
        Film film1 = film.toBuilder().name("blockbuster").build();
        filmDbStorage.updateFilm(film1);
        assertThat(filmDbStorage.getFilmById(film.getId()))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film1);
    }

    @Test
    public void deleteFilmByIdTest() {
        filmDbStorage.createFilm(film);
        assertEquals(filmDbStorage.getFilms().size(), 1);
        filmDbStorage.deleteFilmById(film.getId());
        assertEquals(filmDbStorage.getFilms().size(), 0);
    }

    @Test
    public void getSortedFilmsTest() {
        Director director = new Director(1, "director");
        Film film2 = Film.builder()
                .name("film")
                .description("desc")
                .duration(100)
                .releaseDate(LocalDate.of(2040, 12, 12))
                .mpa(new Mpa(1, "G"))
                .build();

        filmDbStorage.createFilm(film);
        filmDbStorage.createFilm(film2);
        directorDbStorage.createDirector(director);

        film.setDirectors(Arrays.asList(director));
        film2.setDirectors(Arrays.asList(director));
        directorDbStorage.updateFilmDirector(film);
        directorDbStorage.updateFilmDirector(film2);
        List<Film> beforeSorted = Arrays.asList(film2, film);
        List<Film> yearSorted = filmDbStorage.getSortedFilms(1, "year");
        yearSorted.get(1).setDirectors(directorDbStorage.getDirectorsListById(1));
        assertEquals(beforeSorted.get(0), yearSorted.get(1));
    }
}
