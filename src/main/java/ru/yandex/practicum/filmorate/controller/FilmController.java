package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validators.Marker;

import javax.validation.Valid;
import java.util.*;


@RestController
@RequestMapping("/films")
@Slf4j
@Validated
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int counter;


    @GetMapping
    public List<Film> getFilms() {
        log.info("Получен запрос списка фильмов.");
        log.info("Общее кол-во пользователей {}", films.size());
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@RequestBody @Valid Film film) {
        film.setId(++counter);
        films.put(film.getId(), film);
        log.info("Фильм {} с {} добавлен.", film.getName(), film.getId());
        return film;
    }

    @PutMapping
    @Validated({Marker.OnUpdate.class})
    public Film update(@RequestBody @Valid Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Фильм {} с id {} обновлен.", film.getName(), film.getId());
        } else {
            throw new ValidationException("Фильма с id " + film.getId() + " не найден.");
        }
        return film;
    }


}
