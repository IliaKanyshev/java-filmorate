package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public void deleteUser(Integer id) {
        userStorage.deleteUserById(id);
    }

    public User findUserById(Integer userId) {
        return userStorage.findUserById(userId);
    }

    public User addFriend(Integer userId, Integer friendId) {
        User user = findUserById(userId);
        User friend = findUserById(friendId);
        if (user != null && friend != null) {
            user.getFriends().add(friendId);
            friend.getFriends().add(userId);
        }
        log.info("Пользователь {} стал другом с {}", findUserById(userId).getName(), findUserById(friendId).getName());
        return findUserById(userId);
    }

    public User deleteFriend(Integer userId, Integer friendId) {
        findUserById(userId).getFriends().remove(friendId);
        findUserById(friendId).getFriends().remove(userId);
        log.info("Пользователи {} и {} перестали дружить.", findUserById(userId).getName(), findUserById(friendId).getName());
        return findUserById(userId);
    }

    public List<User> getUserFriends(Integer id) {
        log.info("Список друзей пользователя с id {}", id);
        return findUserById(id).getFriends().stream().map(this::findUserById).collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Integer userId, Integer user2Id) {
        log.info("Список общих друзей пользователей {} и {}", userId, user2Id);
        return findUserById(userId).getFriends().stream()
                .filter(findUserById(user2Id).getFriends()::contains)
                .map(this::findUserById)
                .collect(Collectors.toList());
    }
}
