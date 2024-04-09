package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.EventLog;
import ru.yandex.practicum.filmorate.service.log.LogService;

import java.util.List;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class LogController {
    private final LogService logService;

    @GetMapping("/{id}/feed")
    public List<EventLog> getLogs(@PathVariable Integer id) {
        return logService.getLogs(id);
    }

}
