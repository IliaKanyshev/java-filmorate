package ru.yandex.practicum.filmorate.dbstorage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserDbStorageTest {
    private final UserDbStorage userStorage;
    private User user1;
    private User user3;

    @BeforeEach
    public void init() {
        user1 = User.builder()
                .id(1)
                .email("vasya@mail.ru")
                .login("vasya")
                .name("vasek")
                .birthday(LocalDate.of(2020, 12, 12))
                .friends(new HashSet<>())
                .build();
        user3 = User.builder()
                .id(2).email("petya@mail.ru")
                .login("petya")
                .name("Peter")
                .birthday(LocalDate.of(2000, 12, 12))
                .friends(new HashSet<>())
                .build();
    }

    @Test
    public void findUserByIdTest() {
        userStorage.createUser(user1);
        User savedUser = userStorage.findUserById(1);
        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(user1);
    }

    @Test
    public void createUserTest() {
        userStorage.createUser(user1);
        assertEquals(userStorage.getUsers().size(), 1);
        User savedUser = userStorage.findUserById(1);
        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(user1);
    }

    @Test
    public void updateUserTest() {
        userStorage.createUser(user1);
        User user2 = user1.toBuilder().login("petya").build();
        userStorage.updateUser(user2);
        User result = userStorage.findUserById(1);
        assertThat(result)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(user2);
    }

    @Test
    public void getUsersTest() {
        userStorage.createUser(user1);
        userStorage.createUser(user3);
        assertEquals(userStorage.getUsers().size(), 2);
        List<User> users = List.of(user1, user3);
        List<User> users1 = userStorage.getUsers();
        assertThat(users)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(users1);
    }

    @Test
    public void deleteUserTest() {
        userStorage.createUser(user1);
        assertEquals(userStorage.getUsers().size(), 1);
        userStorage.deleteUserById(1);
        assertEquals(userStorage.getUsers().size(), 0);
    }
}
