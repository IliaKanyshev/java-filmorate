package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.*;


@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int counter;

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@RequestBody @Valid User user) {
        nameCheck(user);
        user.setId(++counter);
        users.put(user.getId(), user);
        log.info("Пользователь {} с id {} добавлен.", user.getName(), user.getId());
        return user;
    }

    @PutMapping
    public User update(@RequestBody @Valid User user) {
        nameCheck(user);
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("Пользователь {} с id {} обновлен.", user.getName(), user.getId());
        } else {
            throw new ValidationException("Пользователь с id " + user.getId() + " не найден.");
        }
        return user;
    }

    public void nameCheck(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя не было введено или было пустым. Используется логин.");
        }
    }
}
