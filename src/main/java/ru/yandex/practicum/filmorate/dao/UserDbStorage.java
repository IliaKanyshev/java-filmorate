package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.mapper.UserMapper;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
@Primary
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;

    @Override
    public User createUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("USERS")
                .usingGeneratedKeyColumns("USER_ID");
        int key = simpleJdbcInsert.executeAndReturnKey(userToMap(user)).intValue();
        user.setId(key);
        log.debug("Добавлен пользователь {} с ID {}.", user.getLogin(), user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        findUserById(user.getId());
        String sqlQuery = "update USERS SET EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? where USER_ID = ?";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        log.info("Пользователь с id {} обновлен.", user.getId());
        return findUserById(user.getId());
    }

    @Override
    public List<User> getUsers() {
        log.info("Список всех пользователей:");
        String sqlQuery = "SELECT * FROM USERS";
        return jdbcTemplate.query(sqlQuery, userMapper);
    }

    @Override
    public User findUserById(Integer id) {
        String sqlQuery = "SELECT * FROM USERS WHERE USER_ID = ?";
        try {
            User user = jdbcTemplate.queryForObject(sqlQuery, userMapper, id);
            log.info("Пользователь с id {} найден", id);
            return user;
        } catch (RuntimeException e) {
            log.info("Пользователь с id {} не найден.", id);
            throw new NotFoundException(String.format("Пользователь с id %d не найден", id));
        }
    }

    @Override
    public void deleteUserById(Integer id) {
        String sqlQuery = "DELETE FROM USERS WHERE USER_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
        log.info("Пользователь с id {} удален.", id);
    }

    public Map<String, Object> userToMap(User user) {
        Map<String, Object> values = new HashMap<>();
        values.put("EMAIL", user.getEmail());
        values.put("LOGIN", user.getLogin());
        values.put("NAME", user.getName());
        values.put("BIRTHDAY", user.getBirthday());
        return values;
    }


}
