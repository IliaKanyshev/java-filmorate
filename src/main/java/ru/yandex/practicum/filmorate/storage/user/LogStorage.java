package ru.yandex.practicum.filmorate.storage.user;

import java.util.List;

import ru.yandex.practicum.filmorate.model.EventLog;

public interface LogStorage {

    void saveLog(Integer userId, Integer entityId, String eventType, String operation);

    List<EventLog> getLogs(int eventId);
}
