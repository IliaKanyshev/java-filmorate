package ru.yandex.practicum.filmorate.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmValidationTest {
    private static final Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    private Film film;

    @BeforeEach
    public void init() {
        film = Film.builder().id(0).name(" ").description("").releaseDate(null).duration(-200).build();
    }

    @Test
    public void validateFilmOK() {
        film.toBuilder()
                .id(1)
                .name("film")
                .description("bestFilm")
                .releaseDate(LocalDate.of(2000, 12, 12))
                .duration(100)
                .build();
        validator.validate(film);
    }

    @Test
    public void validatorsTest() {
        Set<ConstraintViolation<Film>> validates = validator.validate(film);
        assertEquals(5, validates.size());
        validates.stream().map(ConstraintViolation::getMessage)
                .forEach(System.out::println);
        Film film1 = Film.builder().releaseDate(LocalDate.of(1800, 12, 12)).build();
        Set<ConstraintViolation<Film>> validates1 = validator.validate(film1);
        validates1.stream().map(ConstraintViolation::getMessage)
                .forEach(System.out::println);
    }
}
