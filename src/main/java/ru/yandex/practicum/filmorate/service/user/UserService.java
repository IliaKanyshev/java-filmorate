package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
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
        return userStorage.addFriend(userId, friendId);
    }

    public User deleteFriend(Integer userId, Integer friendId) {
        return userStorage.deleteFriend(userId, friendId);
    }

    public List<User> getUserFriends(Integer id) {
        return userStorage.getUserFriends(id);
    }

    public List<User> getCommonFriends(Integer userId, Integer user2Id) {
        return userStorage.getCommonFriends(userId, user2Id);
    }
}
