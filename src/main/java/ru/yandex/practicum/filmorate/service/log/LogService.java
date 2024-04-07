package ru.yandex.practicum.filmorate.service.log;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.EventLog;
import ru.yandex.practicum.filmorate.storage.user.LogStorage;

@Component
@RequiredArgsConstructor
@Slf4j
public class LogService {
    private final LogStorage logStorage;

    public List<EventLog> getLogs(Integer id) {
        return logStorage.getLogs(id);
    }
}
