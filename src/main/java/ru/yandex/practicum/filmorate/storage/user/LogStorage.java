package ru.yandex.practicum.filmorate.storage.user;

public interface LogStorage {

    void saveLog(Integer userId, Integer entityId, String eventType, String operation);
}
