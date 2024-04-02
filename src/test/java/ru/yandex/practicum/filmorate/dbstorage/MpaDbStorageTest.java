package ru.yandex.practicum.filmorate.dbstorage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.MpaStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;


@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MpaDbStorageTest {
    private final MpaStorage mpaStorage;
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
             //   .genres(List.of(new Genre(1, "Комедия")))
                .build();
    }

    @Test
    public void getMpaByIdTest() {
        filmDbStorage.createFilm(film);
        Mpa mpa = mpaStorage.getMpaById(film.getId());
        assertEquals(mpa, film.getMpa());
    }

    @Test
    public void getMpaListTest() {
        assertEquals(mpaStorage.getMpaList().size(), 5);
    }
}
