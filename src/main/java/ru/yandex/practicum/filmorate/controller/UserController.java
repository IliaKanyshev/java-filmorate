package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.validators.Marker;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;


@RestController
@RequestMapping("/users")
@Slf4j
@Validated
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> getUsers() {
        log.info("Получен запрос списка пользователей.");
        return userService.getUsers();
    }

    @PostMapping
    public User createUser(@RequestBody @Valid User user) {
        log.info("Получен запрос на создание нового пользователя.");
        return userService.createUser(user);
    }

    @PutMapping
    @Validated({Marker.OnUpdate.class})
    public User updateUser(@RequestBody @Valid User user) {
        log.info("Получен запрос на обновление пользователя.");
        return userService.updateUser(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        log.info("Получен запрос на удаление пользователя с id {}", id);
        userService.deleteUser(id);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Integer id) {
        log.info("Получен запрос на получение пользователя с id {}", id);
        return userService.findUserById(id);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public User addFriend(@PathVariable Integer userId, @PathVariable Integer friendId) {
        log.info("Получен запрос на добавление в друзья. Id пользователя {}, Id друга {}", userId, friendId);
        return userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public User deleteFriend(@PathVariable Integer userId, @PathVariable Integer friendId) {
        log.info("Получен запрос на удаление из друзей. Id пользователя {}, Id друга {}", userId, friendId);
        return userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Integer id) {
        log.info("Получен запрос списка друзей пользователя с id {}.", id);
        return userService.getUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        log.info("Получен запрос на получение списка общих друзей пользователей с id {} и {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("/{userId}/recommendations")
    public List<Film> getRecommendations(@NotNull @PathVariable Integer userId) {
        log.info("Получен запрос списка рекомендаций пользователя с id {}", userId);
        return userService.getRecommendations(userId);
    }

}
