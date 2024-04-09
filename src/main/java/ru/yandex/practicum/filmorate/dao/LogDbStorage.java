package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.EventLog;
import ru.yandex.practicum.filmorate.storage.user.LogStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class LogDbStorage implements LogStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void saveLog(Integer userId, Integer entityId, String eventType, String operation) {
        String sql = "INSERT INTO EVENTS (USER_ID,ENTITY_ID,EVENT_TYPE,OPERATION,EVENT_TIMESTAMP) values (?, ?,?, ?,?)";
        LocalDateTime localDateTime = LocalDateTime.now();
        long timestamp = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        jdbcTemplate.update(sql, userId, entityId, eventType, operation, timestamp);
    }

    @Override
    public List<EventLog> getLogs(int userId) {
        String sql = "SELECT * FROM EVENTS e WHERE USER_ID =?";
        return jdbcTemplate.query(sql, this::logMapper, userId);
    }

    EventLog logMapper(ResultSet rs, int rn) throws SQLException {
        return EventLog.builder()
                .eventId(rs.getInt("EVENT_ID"))
                .userId(rs.getInt("USER_ID"))
                .entityId(rs.getInt("ENTITY_ID"))
                .eventType(rs.getString("EVENT_TYPE"))
                .operation(rs.getString("OPERATION"))
                .timestamp(rs.getLong("EVENT_TIMESTAMP"))
                .build();
    }

}
