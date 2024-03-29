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
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    private User user;
    private User user2;
    private User user3;
    private User user4;

    @BeforeEach
    public void init() {
        user = User.builder()
                .id(1)
                .email("vasya@mail.ru")
                .login("vasya")
                .name("vasek")
                .birthday(LocalDate.of(2020, 12, 12))
                .friends(new HashSet<>())
                .build();
        user2 = User.builder()
                .id(1)
                .email("vasya1@mail.ru")
                .login("Vasya1")
                .name("Vasya1")
                .birthday(LocalDate.of(1991, 12, 12))
                .friends(new HashSet<>())
                .build();
        user3 = User.builder()
                .id(3)
                .login("log in")
                .email("mail.ru")
                .birthday(LocalDate.of(2256, 12, 12))
                .friends(new HashSet<>())
                .build();
        user4 = User.builder()
                .id(4)
                .login("user4")
                .email("user4@mail.ru")
                .birthday(LocalDate.of(2000, 12, 12))
                .friends(new HashSet<>())
                .build();
    }

    @SneakyThrows
    @Test
    public void validationExceptionMessagesTest() {
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user3))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Пробел в логине.")))
                .andExpect(jsonPath("$.error", containsString("День рождения не может быть в будущем.")))
                .andExpect(jsonPath("$.error", containsString("Неверный формат email.")));
        user = user.toBuilder().login("").build();
        mockMvc.perform(put("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", containsString("Пустое поле login")));
    }

    @SneakyThrows
    @Test
    public void shouldNotAddInvalidUserTest() {
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user3))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()));
        mockMvc.perform(get("/users")).andExpect(jsonPath("$.*", hasSize(0)));

    }

    @SneakyThrows
    @Test
    public void userCreateAndFriendAddTest() {
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.email").value("vasya@mail.ru"))
                .andExpect(jsonPath("$.login").value("vasya"))
                .andExpect(jsonPath("$.name").value("vasek"))
                .andExpect(jsonPath("$.birthday").value("2020-12-12"))
                .andExpect(jsonPath("$.friends").isEmpty())
                .andExpect(status().is(200));
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
        mockMvc.perform(put("/users/1/friends/2")
                .content(objectMapper.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(get("/users/1/friends")).andExpect(jsonPath("$.*", hasSize(1)));
    }

    @SneakyThrows
    @Test
    public void getUsersTest() {
        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(get("/users")).andExpect(jsonPath("$.*", hasSize(1)));
    }

    @SneakyThrows
    @Test
    public void updateUserTest() {
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("vasek"));
        user = user.toBuilder().name("Petya").build();
        mockMvc.perform(put("/users").content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(get("/users/1")).andExpect(jsonPath("$.name").value("Petya"));
    }

    @SneakyThrows
    @Test
    public void deleteUserTest() {
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().is(200));
        mockMvc.perform(get("/users")).andExpect(jsonPath("$.*", hasSize(0)));
    }

    @SneakyThrows
    @Test
    public void getUserTest() {
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
        mockMvc.perform(get("/users/1"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.email").value("vasya@mail.ru"))
                .andExpect(jsonPath("$.login").value("vasya"))
                .andExpect(jsonPath("$.name").value("vasek"))
                .andExpect(jsonPath("$.birthday").value("2020-12-12"))
                .andExpect(jsonPath("$.friends").isEmpty())
                .andExpect(status().is(200));
    }

    @SneakyThrows
    @Test
    public void deleteFriendTest() {
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
        mockMvc.perform(put("/users/1/friends/2")
                .content(objectMapper.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(get("/users/1/friends")).andExpect(jsonPath("$.*", hasSize(1)));
        mockMvc.perform(delete("/users/1/friends/2"))
                .andExpect(status().is(200));
        mockMvc.perform(get("/users/1/friends")).andExpect(jsonPath("$.*", hasSize(0)));
    }

    @SneakyThrows
    @Test
    public void getFriendsTest() {
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
        mockMvc.perform(put("/users/1/friends/2")
                .content(objectMapper.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(get("/users/1/friends")).andExpect(jsonPath("$.*", hasSize(1)));
    }

    @SneakyThrows
    @Test
    public void getCommonFriendsTest() {
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user4))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
        mockMvc.perform(put("/users/1/friends/3"))
                .andExpect(status().is(200));
        mockMvc.perform(put("/users/2/friends/3"))
                .andExpect(status().is(200));
        mockMvc.perform(get("/users/1/friends/common/2"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.*", hasSize(1)));
    }
}
