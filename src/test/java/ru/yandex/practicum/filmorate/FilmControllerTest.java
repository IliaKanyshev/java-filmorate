package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FilmControllerTest {
    Film film;
    FilmController filmController = new FilmController();
    private static Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @BeforeEach
    public void init() {
        film = Film.builder().id(0).name(" ").description("").releaseDate(null).duration(-200).build();
    }

    @Test
    public void validatorsTest() {
        Set<ConstraintViolation<Film>> validates = validator.validate(film);
        assertEquals(4, validates.size());
        validates.stream().map(ConstraintViolation::getMessage)
                .forEach(System.out::println);
    }

    @Test
    public void releaseDateException() {
        film.setReleaseDate(LocalDate.of(1800, 12, 12));
        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertEquals("Дата релиза не может быть раньше 28.12.1895.", exception.getMessage());
        assertEquals(0, filmController.getFilms().size());
    }
}
