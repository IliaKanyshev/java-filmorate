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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DirectorDbStorageTest {
    private final DirectorDbStorage directorDbStorage;
    private final FilmDbStorage filmDbStorage;
    private Director director;

    @BeforeEach
    void setUp() {
        director = Director.builder()
                .id(1)
                .name("DirectorTest")
                .build();
        directorDbStorage.createDirector(director);
    }

    @Test
    void getAllDirectorsTest() {
        List<Director> allDirectors = directorDbStorage.getAllDirectors();
        directorDbStorage.createDirector(new Director(2, "Director2"));
        List<Director> allDirectorsAfter = directorDbStorage.getAllDirectors();
        assertEquals(1, allDirectors.size());
        assertEquals(2, allDirectorsAfter.size());
    }

    @Test
    void getByIdTest() {
        assertEquals(director, directorDbStorage.getById(director.getId()).get());
    }

    @Test
    void createDirectorTest() {
        assertTrue(directorDbStorage.getAllDirectors().contains(director));
    }

    @Test
    void updateTest() {
        director.setName("UpdateDirector");
        directorDbStorage.update(director);
        Director updatedDirector = directorDbStorage.getById(director.getId()).get();
        assertTrue(updatedDirector.getName().equals("UpdateDirector"));
    }

    @Test
    void deleteByIdTest() {
        List<Director> allDirectors = directorDbStorage.getAllDirectors();
        assertEquals(allDirectors.size(), 1);
        directorDbStorage.deleteById(director.getId());
        assertTrue(directorDbStorage.getAllDirectors().isEmpty());
    }

    @Test
    void getDirectorsListByIdAndUpdateFilmDirectorTest() {
        Film film = Film.builder()
                .id(1)
                .name("film")
                .description("desc")
                .duration(100)
                .releaseDate(LocalDate.of(2020, 12, 12))
                .mpa(new Mpa(1, "G"))
                .directors(Arrays.asList(new Director(1, "name")))
                .build();
        filmDbStorage.createFilm(film);
        List<Director> directorsListById = directorDbStorage.getDirectorsListById(film.getId());
        assertEquals(0, directorsListById.size());
        directorDbStorage.updateFilmDirector(film);
        List<Director> directorsListByIdAfter = directorDbStorage.getDirectorsListById(film.getId());
        assertEquals(1, directorsListByIdAfter.size());
    }
}