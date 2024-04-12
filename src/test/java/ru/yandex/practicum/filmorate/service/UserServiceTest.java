package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserServiceTest {
    private final UserService userService;
    private User user;
    private User user2;
    private User user3;
    private User user4;

    @BeforeEach
    public void init() {
        user = User.builder()
                .id(1)
                .email("vasya@mail.ru")
                .login("Vasya")
                .birthday(LocalDate.of(1990, 12, 12))
                .friends(new HashSet<>())
                .build();
        user2 = User.builder()
                .id(2)
                .email("vasya1@mail.ru")
                .name("Vas1")
                .login("Vasya1")
                .birthday(LocalDate.of(1991, 12, 12))
                .friends(new HashSet<>())
                .build();
        user3 = User.builder()
                .id(3)
                .name("vas")
                .login(" ")
                .email("mail.ru")
                .birthday(LocalDate.of(2256, 12, 12))
                .build();
        user4 = User.builder()
                .id(4)
                .login("user4")
                .email("user4@mail.ru")
                .birthday(LocalDate.of(2000, 12, 12))
                .build();
    }

    @Test
    public void shouldUseLoginCreateUserNoName() {
        userService.createUser(user);
        assertEquals(userService.findUserById(1).getName(), "Vasya");
    }

    @Test
    public void getUsersTest() {
        userService.createUser(user);
        userService.createUser(user2);
        assertEquals(userService.getUsers().size(), 2);
    }

    @Test
    public void createUserTest() {
        userService.createUser(user2);
        assertEquals(userService.getUsers().size(), 1);
    }

    @Test
    public void updateUserTest() {
        userService.createUser(user);
        user = user.toBuilder().name("newUser").build();
        userService.updateUser(user);
        assertEquals(userService.findUserById(1).getName(), "newUser");
    }

    @Test
    public void deleteUserTest() {
        userService.createUser(user);
        assertEquals(userService.getUsers().size(), 1);
        userService.deleteUser(1);
        assertEquals(userService.getUsers().size(), 0);
    }

    @Test
    public void findUserByIdTest() {
        userService.createUser(user);
        userService.createUser(user2);
        assertEquals(userService.findUserById(1), user);
        assertEquals(userService.findUserById(2), user2);
    }

    @Test
    public void addFriendTest() {
        userService.createUser(user);
        userService.createUser(user2);
        userService.addFriend(1, 2);
        assertEquals(userService.findUserById(1).getFriends().size(), 1);
        assertEquals(userService.findUserById(2).getFriends().size(), 0);
    }

    @Test
    public void deleteFriendTest() {
        userService.createUser(user);
        userService.createUser(user2);
        userService.addFriend(1, 2);
        assertEquals(userService.findUserById(1).getFriends().size(), 1);
        userService.deleteFriend(1, 2);
        assertEquals(userService.findUserById(1).getFriends().size(), 0);
    }

    @Test
    public void getUserFriendsTest() {
        userService.createUser(user);
        userService.createUser(user2);
        userService.addFriend(1, 2);
        assertEquals(userService.getUserFriends(1).size(), 1);
    }

    @Test
    public void getCommonFriendsTest() {
        userService.createUser(user);
        userService.createUser(user2);
        userService.createUser(user4);
        userService.addFriend(1, 2);
        userService.addFriend(3, 2);
        assertEquals(userService.getCommonFriends(1, 3).size(), 1);
        assertTrue(userService.getCommonFriends(1, 3).contains(user2));
    }
}
