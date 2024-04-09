package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.*;
import ru.yandex.practicum.filmorate.storage.user.LogStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeStorage likeStorage;
    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage;
    private final LogStorage logStorage;
    private final FilmSearch filmSearch;

    public List<Film> getFilms() {
        List<Film> films = filmStorage.getFilms();
        for (Film film : films) {
            film.setGenres(genreStorage.getGenreListById(film.getId()));
            film.setLikes(likeStorage.getLikesById(film.getId()));
            film.setDirectors(directorStorage.getDirectorsListById(film.getId()));
        }
        return films;
    }

    public Film createFilm(Film film) {
        if (film.getMpa().getId() > 5) {
            throw new ValidationException("Неверный mpa_id.");
        }
        Film film1 = filmStorage.createFilm(film);
        genreStorage.updateFilmGenres(film1);
        directorStorage.updateFilmDirector(film1);
        return film1;
    }

    public Film updateFilm(Film film) {
        directorStorage.updateFilmDirector(film);
        genreStorage.updateFilmGenres(film);
        filmStorage.updateFilm(film);
        film.setGenres(genreStorage.getGenreListById(film.getId()));
        film.setDirectors(directorStorage.getDirectorsListById(film.getId()));
        film.setLikes(likeStorage.getLikesById(film.getId()));
        return getFilm(film.getId());
    }

    public void deleteFilm(Integer id) {
        filmStorage.deleteFilmById(id);
    }

    public Film getFilm(Integer id) {
        Film film = filmStorage.getFilmById(id);
        film.setGenres(genreStorage.getGenreListById(id));
        film.setLikes(likeStorage.getLikesById(id));
        film.setDirectors(directorStorage.getDirectorsListById(id));
        return film;
    }

    public Film like(Integer filmId, Integer userId) {
        filmStorage.getFilmById(filmId);
        userStorage.findUserById(userId);
        likeStorage.like(filmId, userId);
        logStorage.saveLog(userId, filmId, "LIKE", "ADD");
        log.info("Пользователь с id {} поставил лайк фильму {}.", userId, getFilm(filmId).getName());
        return getFilm(filmId);
    }

    public Film deleteLike(Integer filmId, Integer userId) {
        filmStorage.getFilmById(filmId);
        userStorage.findUserById(userId);
        likeStorage.deleteLike(filmId, userId);
        logStorage.saveLog(userId, filmId, "LIKE", "REMOVE");
        log.info("Пользователь с id {} удалил лайк у фильма с id {}.", userId, filmId);
        return getFilm(filmId);
    }

    public List<Film> getPopularFilms(Integer count) {
        log.info("Топ {} популярных фильмов", count);
        List<Film> films = likeStorage.getPopularFilms(count);
        films.forEach(
                film -> {
                    film.getLikes().addAll(likeStorage.getLikesById(film.getId()));
                    film.getGenres().addAll(genreStorage.getGenreListById(film.getId()));
                    film.setDirectors(directorStorage.getDirectorsListById(film.getId()));
                }
        );
        return films;
    }

    public List<Film> getFilmsByDirector(int id, String sort) {
        if (directorStorage.getById(id).isEmpty()) {
            throw new NotFoundException("Нету режиссера с таким id " + id);
        }
        if (!sort.equals("likes") && !sort.equals("year")) {
            throw new NotFoundException("Неправильно передан параметр сортировки");
        }
        List<Film> films = filmStorage.getSortedFilms(id, sort);
        films.forEach(film -> {
            film.setDirectors(directorStorage.getDirectorsListById(film.getId()));
            film.setGenres(genreStorage.getGenreListById(film.getId()));
            film.setLikes(likeStorage.getLikesById(film.getId()));
        });
        log.info("Список отсортированных фильмов по {}", sort);
        return films;
    }

    public List<Film> getFilmsByTitleOrDirector(String query, List<String> by) {
        List<Film> films = new ArrayList<>();
        if (by.contains("director") && by.size() == 1) {
            films = filmSearch.getFilmListByDirector(query);
        } else if (by.contains("title") && by.size() == 1) {
            films = filmSearch.getFilmListByTitle(query);
        } else if (by.contains("director") && by.contains("title") && by.size() == 2) {
            films = filmSearch.getFilmListByTitleAndDirector(query);
        }
        for (Film film : films) {
            film.setGenres(genreStorage.getGenreListById(film.getId()));
            film.setLikes(likeStorage.getLikesById(film.getId()));
            film.setDirectors(directorStorage.getDirectorsListById(film.getId()));
            return films;
        }
        return Collections.emptyList();
    }

    public List<Film> getCommonFilms(int userId, int friendId) {
        userStorage.findUserById(userId);
        userStorage.findUserById(friendId);
        List<Film> commonFilms = filmStorage.getCommonFilms(userId, friendId);
        commonFilms.forEach(film -> {
            film.setDirectors(directorStorage.getDirectorsListById(film.getId()));
            film.setGenres(genreStorage.getGenreListById(film.getId()));
        });
        return commonFilms;
    }

    public List<Film> getPopularFilmsByGenreAndYear(Integer count, Integer genreId, Integer year) {
        List<Film> films = filmStorage.getPopularFilmsByGenreAndYear(count, genreId, year);
        for (Film film : films) {
            film.setGenres(genreStorage.getGenreListById(film.getId()));
            film.setLikes(likeStorage.getLikesById(film.getId()));
            film.setDirectors(directorStorage.getDirectorsListById(film.getId()));
        }
        return films;
    }
}
