package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.genre.GenreService;

import java.util.List;

@RestController
@RequestMapping("/genres")
@Slf4j
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable Integer id) {
        log.info("Получен запрос на получение жанра по id {}", id);
        return genreService.getGenreById(id);
    }

    @GetMapping
    public List<Genre> getGenreList() {
        log.info("Получен запрос списка жанров.");
        return genreService.getGenresList();
    }
}
