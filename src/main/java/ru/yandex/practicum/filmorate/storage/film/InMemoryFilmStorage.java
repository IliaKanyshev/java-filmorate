package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int counter;

    @Override
    public List<Film> getFilms() {
        log.info("Общее кол-во фильмов {}", films.size());
        return new ArrayList<>(films.values());
    }

    @Override
    public Film createFilm(Film film) {
        film.setId(++counter);
        film.setLikes(new HashSet<>());
        films.put(film.getId(), film);
        log.info("Фильм {} с id {} добавлен.", film.getName(), film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            film.setLikes(getFilmById(film.getId()).getLikes());
            films.put(film.getId(), film);
            log.info("Фильм {} с id {} обновлен.", film.getName(), film.getId());
        } else {
            log.warn("Фильм с id {} не найден.", film.getId());
            throw new NotFoundException("Фильма с id " + film.getId() + " не найден.");
        }
        return film;
    }

    @Override
    public void deleteFilmById(Integer id) {
        if (films.containsKey(id)) {
            films.remove(id);
            log.info("Фильм с id {} добавлен.", id);
        } else {
            log.warn("Фильм с id {} не найден.", id);
            throw new NotFoundException(String.format("Фильм с id %d не найден.", id));
        }
    }

    @Override
    public Film getFilmById(Integer id) {
        return films.values().stream()
                .filter(film -> film.getId() == id)
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Фильм с id {} не найден", id);
                    return new NotFoundException(String.format("Фильм с id %d не найден.", id));
                });
    }
}
