package ru.yandex.practicum.filmorate.dao;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.storage.user.LogStorage;


@Component
@RequiredArgsConstructor
@Slf4j
public class LogDbStorage implements LogStorage {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void saveLog(Integer userId, Integer entityId, String eventType, String operation) {
        String sql = "INSERT INTO EVENTS (USER_ID,ENTITY_ID,EVENT_TYPE,OPERATION,EVENT_TIMESTAMP) values (?, ?,?, ?,?)";

        jdbcTemplate.update(sql, userId, entityId, eventType, operation, Timestamp.valueOf(LocalDateTime.now()));
    }

}
