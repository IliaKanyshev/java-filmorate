package ru.yandex.practicum.filmorate.service.log;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.EventLog;
import ru.yandex.practicum.filmorate.storage.user.LogStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class LogService {
    private final LogStorage logStorage;
    private final UserStorage userStorage;

    public List<EventLog> getLogs(Integer id) {
        userStorage.findUserById(id);
        return logStorage.getLogs(id);
    }
}
