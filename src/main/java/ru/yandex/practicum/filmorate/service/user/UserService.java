package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User createUser(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
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
        findUserById(userId);
        findUserById(friendId);
        friendStorage.addFriend(userId, friendId);
        log.info("Пользователь {} стал другом с {}", findUserById(userId).getName(), findUserById(friendId).getName());
        return findUserById(userId);
    }

    public User deleteFriend(Integer userId, Integer friendId) {
        friendStorage.deleteFriend(userId, friendId);
        log.info("Пользователи {} и {} перестали дружить.", findUserById(userId).getName(), findUserById(friendId).getName());
        return findUserById(userId);
    }

    public List<User> getUserFriends(Integer id) {
        log.info("Список друзей пользователя с id {}", id);
        return friendStorage.getUserFriends(id);
    }

    public List<User> getCommonFriends(Integer userId, Integer user2Id) {
        log.info("Список общих друзей пользователей {} и {}", userId, user2Id);
        return friendStorage.getCommonFriends(userId, user2Id);
    }
}
