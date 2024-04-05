package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmSearch {
    List<Film> getFilmListByTitle(String query);

    List<Film> getFilmListByDirector(String query);

    List<Film> getFilmListByTitleAndDirector(String query);
}
