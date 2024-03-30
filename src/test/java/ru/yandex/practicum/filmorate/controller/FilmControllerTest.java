package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class FilmControllerTest {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    private Film film;
    private Film film2;
    private Film film3;
    private Film film4;
    private User user;
    private User user2;

    @BeforeEach
    public void init() {
        film = Film.builder()
                .id(1)
                .name("film")
                .description("desc")
                .duration(100)
                .releaseDate(LocalDate.of(2020, 12, 12))
                .mpa(new Mpa(1, "G"))
                .genres(List.of(new Genre(1, "Комедия")))
                .likes(new HashSet<>())
                .build();
        film2 = Film.builder()
                .id(1)
                .name("film1")
                .description("desc1")
                .duration(100)
                .releaseDate(LocalDate.of(2020, 12, 12))
                .mpa(new Mpa(1, "G"))
                .genres(List.of(new Genre(1, "Комедия")))
                .likes(new HashSet<>())
                .build();
        film3 = Film.builder()
                .id(3)
                .name(" ")
                .description("filmDescr3aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                .releaseDate(LocalDate.of(1852, 12, 12))
                .duration(0)
                .build();
        film4 = Film.builder()
                .id(1)
                .name("film4")
                .description("desc4")
                .duration(100)
                .releaseDate(LocalDate.of(2020, 12, 12))
                .mpa(new Mpa(1, "G"))
                .genres(List.of(new Genre(1, "Комедия")))
                .likes(new HashSet<>())
                .build();
        user = User.builder()
                .id(1)
                .email("vasya@mail.ru")
                .login("Vasya")
                .birthday(LocalDate.of(1990, 12, 12))
                .build();
        user2 = User.builder()
                .id(2)
                .email("vasya1@mail.ru")
                .login("Vasya1")
                .birthday(LocalDate.of(1991, 12, 12))
                .build();
    }

    @SneakyThrows
    @Test
    public void shouldNotAddInvalidFilmTest() {
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film3))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()));
        mockMvc.perform(get("/films")).andExpect(jsonPath("$.*", hasSize(0)));
    }

    @SneakyThrows
    @Test
    public void validationExceptionMessageTest() {
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film3))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Пустое поле name")))
                .andExpect(jsonPath("$.error", containsString("Длина описания не может превышать 200 символов.")))
                .andExpect(jsonPath("$.error", containsString("Дата релиза не может быть раньше 28.12.1895.")));
        film = film.toBuilder().releaseDate(null).build();
        mockMvc.perform(put("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", containsString("Поле releaseDate == null")));
    }

    @SneakyThrows
    @Test
    public void filmCreateTest() {
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("film"))
                .andExpect(jsonPath("$.description").value("desc"))
                .andExpect(jsonPath("$.releaseDate").value("2020-12-12"))
                .andExpect(jsonPath("$.duration").value(100))
                .andExpect(jsonPath("$.likes").isEmpty())
                .andExpect(status().is(200));
    }

    @SneakyThrows
    @Test
    public void getFilmsTest() {
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
        mockMvc.perform(get("/films")).andExpect(jsonPath("$.*", hasSize(2)));
    }

    @SneakyThrows
    @Test
    public void updateFilmTest() {
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.name").value("film"));
        film = film.toBuilder().name("newName").build();
        mockMvc.perform(put("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
        mockMvc.perform(get("/films/1"))
                .andExpect(jsonPath("$.name").value("newName"));
    }

    @SneakyThrows
    @Test
    public void deleteFilmTest() {
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
        mockMvc.perform(get("/films")).andExpect(jsonPath("$.*", hasSize(1)));
        mockMvc.perform(delete("/films/1")).andExpect(status().is(200));
        mockMvc.perform(get("/films")).andExpect(jsonPath("$.*", hasSize(0)));
    }

    @SneakyThrows
    @Test
    public void getFilmTest() {
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
        mockMvc.perform(get("/films/1"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("film"))
                .andExpect(jsonPath("$.description").value("desc"))
                .andExpect(jsonPath("$.releaseDate").value("2020-12-12"))
                .andExpect(jsonPath("$.duration").value(100))
                .andExpect(jsonPath("$.likes").isEmpty())
                .andExpect(status().is(200));
    }

    @SneakyThrows
    @Test
    public void likeTest() {
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
        mockMvc.perform(put("/films/1/like/1"))
                .andExpect(status().is(200));
        mockMvc.perform(get("/films/1"))
                .andExpect(jsonPath("$.likes", hasSize(1)));
    }

    @SneakyThrows
    @Test
    public void deleteLikeTest() {
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
        mockMvc.perform(put("/films/1/like/1"))
                .andExpect(status().is(200));
        mockMvc.perform(get("/films/1"))
                .andExpect(jsonPath("$.likes", hasSize(1)));
        mockMvc.perform(delete("/films/1/like/1"))
                .andExpect(status().is(200));
        mockMvc.perform(get("/films/1"))
                .andExpect(jsonPath("$.likes", hasSize(0)));
    }

    @SneakyThrows
    @Test
    public void getPopularFilmsTest() {
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film4))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
        mockMvc.perform(put("/films/1/like/1"))
                .andExpect(status().is(200));
        mockMvc.perform(put("/films/2/like/1"))
                .andExpect(status().is(200));
        mockMvc.perform(put("/films/3/like/1"))
                .andExpect(status().is(200));
        mockMvc.perform(get("/films/popular"))
                .andExpect(jsonPath("$.*", hasSize(3)));
    }
}
