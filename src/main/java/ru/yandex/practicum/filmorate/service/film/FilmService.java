package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;
import ru.yandex.practicum.filmorate.storage.film.LikeStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeStorage likeStorage;
    private final GenreStorage genreStorage;

    public List<Film> getFilms() {
        List<Film> films = filmStorage.getFilms();
        for (Film film : films) {
            film.getGenres().addAll(genreStorage.getGenreListById(film.getId()));
            film.getLikes().addAll(likeStorage.getLikesById(film.getId()));
        }
        return films;
    }

    public Film createFilm(Film film) {
        if (film.getMpa().getId() > 5) {
            throw new ValidationException("Неверный mpa_id.");
        }
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        film.getGenres().addAll(genreStorage.getGenreListById(film.getId()));
        film.getLikes().addAll(likeStorage.getLikesById(film.getId()));
        return filmStorage.updateFilm(film);
    }

    public void deleteFilm(Integer id) {
        filmStorage.deleteFilmById(id);
    }

    public Film getFilm(Integer id) {
        Film film = filmStorage.getFilmById(id);
        film.getGenres().addAll(genreStorage.getGenreListById(film.getId()));
        film.getLikes().addAll(likeStorage.getLikesById(film.getId()));
        return film;
    }

    public Film like(Integer filmId, Integer userId) {
        filmStorage.getFilmById(filmId);
        userStorage.findUserById(userId);
        likeStorage.like(filmId, userId);
        log.info("Пользователь с id {} поставил лайк фильму {}.", userId, getFilm(filmId).getName());
        return getFilm(filmId);
    }

    public Film deleteLike(Integer filmId, Integer userId) {
        filmStorage.getFilmById(filmId);
        userStorage.findUserById(userId);
        likeStorage.deleteLike(filmId, userId);
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
                }
        );
        return films;
    }
}
