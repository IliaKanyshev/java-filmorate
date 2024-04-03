package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {
    List<Director> getAllDirectors();
   Optional<Director> getById(int id);
    Director createDirector(Director director);
    Director update(Director director);
    void deleteById(int id);
}
