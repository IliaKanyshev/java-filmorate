package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getFilms();

    Film createFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilmById(Integer id);

    Film getFilmById(Integer id);

    List<Film> getSortedFilms(int id, String sort);

    List<Film> getCommonFilms(int userId, int friendId);

    List<Film> getPopularFilmsByGenreAndYear(Integer count, Integer genreId, Integer year);

    List<Film> getRecommendations(Integer userId);
}
