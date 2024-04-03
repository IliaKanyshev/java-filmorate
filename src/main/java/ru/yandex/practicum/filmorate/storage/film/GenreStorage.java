package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {
    Genre getGenreById(Integer id);

    List<Genre> getGenresList();

    List<Genre> getGenreListById(Integer id);

    List<Integer> getGenresIds();

    void updateFilmGenres(Film film);
}
