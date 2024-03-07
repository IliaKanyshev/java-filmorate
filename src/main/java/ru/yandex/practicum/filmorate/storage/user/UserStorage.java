package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User createUser(User user);

    User updateUser(User user);

    List<User> getUsers();

    User addFriend(Integer userId, Integer friendId);

    User findUserById(Integer id);

    User deleteFriend(Integer userId, Integer friendId);

    List<User> getUserFriends(Integer id);

    List<User> getCommonFriends(Integer userId, Integer user2Id);

    void deleteUserById(Integer id);
}
