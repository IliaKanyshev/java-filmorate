package ru.yandex.practicum.filmorate.dbstorage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.FriendDbStorage;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FriendDbStorageTest {
    private final FriendDbStorage friendDbStorage;
    private final UserDbStorage userDbStorage;
    private User user1;
    private User user3;
    private User user5;

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
        user5 = User.builder()
                .id(3).email("jorik@mail.ru")
                .login("jorik")
                .name("Jora")
                .birthday(LocalDate.of(2000, 12, 12))
                .friends(new HashSet<>())
                .build();
    }

    @Test
    public void addFriendTest() {
        userDbStorage.createUser(user1);
        userDbStorage.createUser(user3);
        friendDbStorage.addFriend(user1.getId(), user3.getId());
        assertEquals(userDbStorage.findUserById(user1.getId()).getFriends().size(), 1);
    }

    @Test
    public void deleteFriend() {
        userDbStorage.createUser(user1);
        userDbStorage.createUser(user3);
        friendDbStorage.addFriend(user1.getId(), user3.getId());
        assertEquals(userDbStorage.findUserById(user1.getId()).getFriends().size(), 1);
        friendDbStorage.deleteFriend(user1.getId(), user3.getId());
        assertEquals(userDbStorage.findUserById(user1.getId()).getFriends().size(), 0);
    }

    @Test
    public void getUserFriendsTest() {
        userDbStorage.createUser(user1);
        userDbStorage.createUser(user3);
        friendDbStorage.addFriend(user1.getId(), user3.getId());
        assertEquals(friendDbStorage.getUserFriends(user1.getId()).size(), 1);
        assertEquals(friendDbStorage.getUserFriends(user3.getId()).size(), 0);
    }

    @Test
    public void getCommonFriendsTest() {
        userDbStorage.createUser(user1);
        userDbStorage.createUser(user3);
        userDbStorage.createUser(user5);
        friendDbStorage.addFriend(user1.getId(), user3.getId());
        friendDbStorage.addFriend(user5.getId(), user3.getId());
        assertEquals(friendDbStorage.getCommonFriends(user1.getId(), user5.getId()).size(), 1);
    }
}
