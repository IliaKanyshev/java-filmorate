package ru.yandex.practicum.filmorate.dao.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

@RequiredArgsConstructor
@Component
public class FilmMapper implements RowMapper<Film> {
/*private final DirectorStorage directorStorage;
private final MpaStorage mpaStorage; */
    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(resultSet.getInt("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(new Mpa(resultSet.getInt("mpa_rating_id"), resultSet.getString("MPA.name")))
                .build();
     /*   Integer mpaId = (Integer) resultSet.getObject("mpa_rating_id");
        if (mpaId != null) {
            film.setMpa(mpaStorage.getMpaById(mpaId));
        }
        Integer directorId = (Integer) resultSet.getObject("director_id");
        if (directorId != null) {
            film.getDirectors().add(directorStorage.getById(directorId).get());
        } */
        return film;
    }
}
