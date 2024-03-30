package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.FriendStorage;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class FriendDbStorage implements FriendStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userDbStorage;

    @Override
    public User addFriend(Integer userId, Integer friendId) {
        userDbStorage.findUserById(userId);
        userDbStorage.findUserById(friendId);
        jdbcTemplate.update("MERGE INTO friends(USER_ID, FRIEND_ID) VALUES(?, ?)", userId, friendId);
        log.info("Пользователь с id {} добавил в друзья пользователя с id {}", userId, friendId);
        userDbStorage.findUserById(userId).setFriends(getUserFriends(userId).stream()
                .map(User::getId)
                .collect(Collectors.toSet()));
        return userDbStorage.findUserById(userId);
    }

    @Override
    public User deleteFriend(Integer userId, Integer friendId) {
        userDbStorage.findUserById(userId);
        userDbStorage.findUserById(friendId);
        jdbcTemplate.update("DELETE FROM friends WHERE USER_ID = ? and FRIEND_ID = ?", userId, friendId);
        userDbStorage.findUserById(userId).setFriends(getUserFriends(userId).stream()
                .map(User::getId)
                .collect(Collectors.toSet()));
        log.info("Пользователь с id {} удалил друга с id {}.", userId, friendId);
        return userDbStorage.findUserById(userId);
    }

    @Override
    public List<User> getUserFriends(Integer id) {
        userDbStorage.findUserById(id);
        String sqlQuery = "select * from USERS, FRIENDS " +
                "where USERS.USER_ID = FRIENDS.FRIEND_ID AND FRIENDS.USER_ID = ?";
        log.info("Список друзей пользователья с id {}", id);
        return jdbcTemplate.query(sqlQuery, userDbStorage::mapRowToUser, id);
    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer friendId) {
        userDbStorage.findUserById(userId);
        userDbStorage.findUserById(friendId);
        String sqlQuery = "select * from USERS u, FRIENDS f, FRIENDS o " +
                "where u.USER_ID = f.FRIEND_ID AND u.USER_ID = o.FRIEND_ID AND f.USER_ID = ? AND o.USER_ID = ?";
        log.info("Список общих друзей пользователей с id {} и {}", userId, friendId);
        return jdbcTemplate.query(sqlQuery, userDbStorage::mapRowToUser, userId, friendId);
    }
}
