package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    User user;
    User user2;

    @Test
    public void friendTest() throws Exception {
        user = User.builder().id(1)
                .email("vasya@mail.ru")
                .login("Vasya")
                .birthday(LocalDate.of(1990, 12, 12))
                .build();
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.email").value("vasya@mail.ru"))
                .andExpect(jsonPath("$.login").value("Vasya"))
                .andExpect(jsonPath("$.name").value("Vasya"))
                .andExpect(jsonPath("$.birthday").value("1990-12-12"))
                .andExpect(jsonPath("$.friends").isEmpty())
                .andExpect(status().is(200));
        user2 = User.builder().id(2)
                .email("vasya1@mail.ru")
                .login("Vasya1")
                .birthday(LocalDate.of(1991, 12, 12))
                .build();
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.email").value("vasya1@mail.ru"))
                .andExpect(jsonPath("$.login").value("Vasya1"))
                .andExpect(jsonPath("$.name").value("Vasya1"))
                .andExpect(jsonPath("$.birthday").value("1991-12-12"))
                .andExpect(jsonPath("$.friends").isEmpty())
                .andExpect(status().is(200));
        mockMvc.perform(put("/users/1/friends/2").content(objectMapper.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON));
    }
}
