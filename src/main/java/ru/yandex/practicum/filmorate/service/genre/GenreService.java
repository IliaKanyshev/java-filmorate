package ru.yandex.practicum.filmorate.service.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenreService {
    private final GenreStorage genreStorage;

    public Genre getGenreById(Integer id) {
        return genreStorage.getGenreById(id);
    }

    public List<Genre> getGenresList() {
        return genreStorage.getGenresList();
    }

    public List<Genre> getGenreListById(Integer id) {
        return genreStorage.getGenreListById(id);
    }
}
