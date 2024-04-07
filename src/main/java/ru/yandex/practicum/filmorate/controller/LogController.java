package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.EventLog;
import ru.yandex.practicum.filmorate.service.log.LogService;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


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
