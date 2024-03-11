package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public void deleteFilm(Integer id) {
        filmStorage.deleteFilmById(id);
    }

    public Film getFilm(Integer id) {
        return filmStorage.getFilmById(id);
    }

    public Film like(Integer filmId, Integer userId) {
        Film film = getFilm(filmId);
        User user = userStorage.findUserById(userId);
        if (film != null && user != null) {
            film.getLikes().add(userId);
        }
        log.info("Пользователь с id {} поставил лайк фильму {}.", userId, film.getName());
        return getFilm(filmId);
    }

    public Film deleteLike(Integer filmId, Integer userId) {
        if (getFilm(filmId).getLikes().contains(userId)) {
            getFilm(filmId).getLikes().remove(userId);
        } else {
            log.warn("Лайк с id {} не найден.", userId);
            throw new NotFoundException(String.format("Лайк с id %d не найден.", userId));
        }
        log.info("Пользователь с id {} удалил лайк у фильма с id {}.", userId, filmId);
        return getFilm(filmId);
    }

    public List<Film> getPopularFilms(Integer count) {
        log.info("Топ {} популярных фильмов", count);
        return getFilms().stream()
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}
