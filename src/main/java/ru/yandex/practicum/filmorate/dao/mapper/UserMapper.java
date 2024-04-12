package ru.yandex.practicum.filmorate.dao.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserMapper implements RowMapper<User> {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("user_id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .friends(getFriendsIds(resultSet.getInt("user_id")))
                .build();
    }

    public Set<Integer> getFriendsIds(Integer id) {
        String sql = "SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = ?";
        Set<Integer> ids = new HashSet<>();
        SqlRowSet friends = jdbcTemplate.queryForRowSet(sql, id);
        while (friends.next()) {
            ids.add(friends.getInt("FRIEND_ID"));
        }
        return ids;
    }
}
