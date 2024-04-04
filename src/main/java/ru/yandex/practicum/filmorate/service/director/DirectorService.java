package ru.yandex.practicum.filmorate.service.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.film.DirectorStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectorService {
    private final DirectorStorage directorStorage;

    public List<Director> getAllDirectors() {
        return directorStorage.getAllDirectors();
    }

    public Director getById(int id) {
        return directorStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Не существует пользователь с таким id " + id));
    }

    public Director createDirector(Director director) {
        return directorStorage.createDirector(director);
    }

    public Director update(Director director) {
        if (directorStorage.getById(director.getId()).isEmpty()) {
            throw new NotFoundException("Такого режиссера не существует");
        }
        return directorStorage.update(director);
    }

    public void deleteById(int id) {
        directorStorage.deleteById(id);
    }
}
