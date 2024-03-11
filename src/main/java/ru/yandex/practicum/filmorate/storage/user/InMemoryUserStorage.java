package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int counter;

    @Override
    public List<User> getUsers() {
        log.info("Кол-во пользователей в списке {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User createUser(User user) {
        nameCheck(user);
        user.setId(++counter);
        user.setFriends(new HashSet<>());
        users.put(user.getId(), user);
        log.info("Пользователь {} с id {} добавлен.", user.getName(), user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        nameCheck(user);
        if (users.containsKey(user.getId())) {
            user.setFriends(users.get(user.getId()).getFriends());
            users.put(user.getId(), user);
            log.info("Пользователь {} с id {} обновлен.", user.getName(), user.getId());
        } else {
            log.warn("Пользователь с id {} не найден.", user.getId());
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден.");
        }
        return user;
    }

    @Override
    public void deleteUserById(Integer id) {
        if (users.containsKey(id)) {
            users.remove(id);
            log.info("Пользователь с id {} удален.", id);
        } else {
            log.warn("Пользователь с id {} не найден.", id);
            throw new NotFoundException(String.format("Пользователь с id %d не найден.", id));
        }
    }

    @Override
    public User findUserById(Integer id) {
        return users.values().stream()
                .filter(user -> user.getId() == id)
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Пользователь с id {} не найден.", id);
                    return new NotFoundException(String.format("Пользователь с id %d не найден.", id));
                });
    }

    public void nameCheck(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя не было введено или было пустым. Используется логин.");
        }
    }
}
