package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;


@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int counter;
    private static final LocalDate MINIMUM_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@RequestBody @Valid Film film) {
        dateCheck(film);
        film.setId(++counter);
        films.put(film.getId(), film);
        log.info("Фильм {} с {} добавлен.", film.getName(), film.getId());
        return film;
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film film) {
        dateCheck(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Фильм {} с id {} обновлен.", film.getName(), film.getId());
        } else {
            throw new ValidationException("Фильма с id " + film.getId() + " не найден.");
        }
        return film;
    }

    public void dateCheck(Film film) {
        if (film.getReleaseDate().isBefore(MINIMUM_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895.");
        }
        log.info("Введена дата релиза до 28.12.1895.");
    }
}
