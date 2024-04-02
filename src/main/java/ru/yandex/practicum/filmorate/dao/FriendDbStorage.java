package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.FriendStorage;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class FriendDbStorage implements FriendStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;
    @Override
    public void addFriend(Integer userId, Integer friendId) {
        jdbcTemplate.update("MERGE INTO friends(USER_ID, FRIEND_ID) VALUES(?, ?)", userId, friendId);
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        jdbcTemplate.update("DELETE FROM friends WHERE USER_ID = ? and FRIEND_ID = ?", userId, friendId);
    }

    @Override
    public List<User> getUserFriends(Integer id) {
        String sqlQuery = "select * from USERS, FRIENDS " +
                "where USERS.USER_ID = FRIENDS.FRIEND_ID AND FRIENDS.USER_ID = ?";
        return jdbcTemplate.query(sqlQuery, userMapper, id);
    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer friendId) {
        String sqlQuery = "select * from USERS u, FRIENDS f, FRIENDS o " +
                "where u.USER_ID = f.FRIEND_ID AND u.USER_ID = o.FRIEND_ID AND f.USER_ID = ? AND o.USER_ID = ?";
        return jdbcTemplate.query(sqlQuery, userMapper, userId, friendId);
    }
}
