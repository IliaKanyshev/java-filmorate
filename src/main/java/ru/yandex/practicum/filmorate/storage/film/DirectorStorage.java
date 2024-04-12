package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DirectorStorage {
    List<Director> getAllDirectors();

    Optional<Director> getById(int id);

    Director createDirector(Director director);

    Director update(Director director);

    void deleteById(int id);

    List<Director> getDirectorsListById(Integer id);

    void updateFilmDirector(Film film);

    Map<Integer, List<Director>> getFilmDirectorsMap();
}
