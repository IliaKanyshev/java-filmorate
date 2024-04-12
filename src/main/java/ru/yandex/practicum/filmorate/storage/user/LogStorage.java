package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.EventLog;

import java.util.List;

public interface LogStorage {

    void saveLog(Integer userId, Integer entityId, String eventType, String operation);

    List<EventLog> getLogs(int eventId);
}
