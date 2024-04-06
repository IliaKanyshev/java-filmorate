package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.validators.Marker;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;


@RestController
@RequestMapping("/films")
@Slf4j
@Validated
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsByDirector(@PathVariable int directorId, @RequestParam String sortBy) {
        log.info("Получен запрос на получения отсортированного списка фильмов по лайкам или id.");
        return filmService.getFilmsByDirector(directorId, sortBy);
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Получен запрос списка фильмов.");
        return filmService.getFilms();
    }

    @PostMapping
    public Film createFilm(@RequestBody @Valid Film film) {
        log.info("Получен запрос на добавление фильма.");
        return filmService.createFilm(film);
    }

    @PutMapping
    @Validated({Marker.OnUpdate.class})
    public Film updateFilm(@RequestBody @Valid Film film) {
        log.info("Получен запрос на обновление фильма.");
        return filmService.updateFilm(film);
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable Integer id) {
        filmService.deleteFilm(id);
        log.info("Фильм с id {} удален.", id);
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Integer id) {
        log.info("Получен запрос на получение фильма с id {}", id);
        return filmService.getFilm(id);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public Film like(@PathVariable Integer filmId, @PathVariable @Positive Integer userId) {
        log.info("Получен запрос на добавление лайка.");
        return filmService.like(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public Film deleteLike(@PathVariable Integer filmId, @PathVariable Integer userId) {
        log.info("Получен запрос на удаление лайка.");
        return filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilmsByGenreAndYear(
            @RequestParam(name = "count", defaultValue = "10", required = false) Integer count,
            @RequestParam(name = "genreId", defaultValue = "0", required = false) Integer genreId,
            @RequestParam(name = "year", defaultValue = "0", required = false) Integer year) {
        if (genreId != 0 || year != 0) {
            return filmService.getPopularFilmsByGenreAndYear(count, genreId, year);
        }
        log.info("Получен запрос на получение списка популярных фильмов.");
        return filmService.getPopularFilms(count);
    }

}
