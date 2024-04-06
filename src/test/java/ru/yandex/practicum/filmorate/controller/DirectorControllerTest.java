package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Director;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class DirectorControllerTest {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;

    @SneakyThrows
    @Test
    void getAllDirectorsTest() {
        Director director = Director.builder()
                .name("DirectorTest")
                .build();
        mockMvc.perform(post("/directors")
                        .content(objectMapper.writeValueAsString(director))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
        mockMvc.perform(get("/directors")).andExpect(jsonPath("$.*", hasSize(1)));
    }

    @SneakyThrows
    @Test
    void badValidationCreateEmptyNameTest() {
        Director director = Director.builder()
                .name("")
                .build();
        mockMvc.perform(post("/directors")
                        .content(objectMapper.writeValueAsString(director))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @SneakyThrows
    @Test
    void createDirectorAndGetByIdTest() {
        Director director = Director.builder()
                .name("DirectorTest")
                .build();
        mockMvc.perform(post("/directors")
                        .content(objectMapper.writeValueAsString(director))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
        mockMvc.perform(get("/directors/1"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.name").value("DirectorTest"));
    }

    @SneakyThrows
    @Test
    void updateTest() {
        Director director = Director.builder()
                .name("DirectorTest")
                .build();
        mockMvc.perform(post("/directors")
                        .content(objectMapper.writeValueAsString(director))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
        director.setName("UpdatedDirector");
        director.setId(1);
        mockMvc.perform(put("/directors")
                        .content(objectMapper.writeValueAsString(director))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
        mockMvc.perform(get("/directors/1"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.name").value("UpdatedDirector"));
    }

    @SneakyThrows
    @Test
    void deleteByIdTest() {
        Director director = Director.builder()
                .name("DirectorTest")
                .build();
        mockMvc.perform(post("/directors")
                        .content(objectMapper.writeValueAsString(director))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
        mockMvc.perform(get("/directors")).andExpect(jsonPath("$.*", hasSize(1)));
        mockMvc.perform(delete("/directors/1"))
                .andExpect(status().is(200));
        mockMvc.perform(get("/directors")).andExpect(jsonPath("$.*", hasSize(0)));
    }
}