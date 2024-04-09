package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;
import ru.yandex.practicum.filmorate.storage.film.LikeStorage;
import ru.yandex.practicum.filmorate.storage.user.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.LogStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;
    private final LogStorage logStorage;
    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;
    private final LikeStorage likeStorage;
    private final DirectorStorage directorStorage;

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
        User user = findUserById(userId);
        User friend = findUserById(friendId);
        friendStorage.addFriend(userId, friendId);
        logStorage.saveLog(userId, friendId, "FRIEND", "ADD");
        user.setFriends(getUserFriends(userId).stream()
                .map(User::getId)
                .collect(Collectors.toSet()));
        log.info("Пользователь {} стал другом с {}", user.getName(), friend.getName());
        return user;
    }

    public User deleteFriend(Integer userId, Integer friendId) {
        User user = findUserById(userId);
        User friend = findUserById(friendId);
        friendStorage.deleteFriend(userId, friendId);
        logStorage.saveLog(userId, friendId, "FRIEND", "REMOVE");
        user.setFriends(getUserFriends(userId).stream()
                .map(User::getId)
                .collect(Collectors.toSet()));
        log.info("Пользователи {} и {} перестали дружить.", user.getName(), friend.getName());
        return user;
    }

    public List<User> getUserFriends(Integer id) {
        userStorage.findUserById(id);
        log.info("Список друзей пользователя с id {}", id);
        return friendStorage.getUserFriends(id);
    }

    public List<User> getCommonFriends(Integer userId, Integer user2Id) {
        userStorage.findUserById(userId);
        userStorage.findUserById(user2Id);
        log.info("Список общих друзей пользователей {} и {}", userId, user2Id);
        return friendStorage.getCommonFriends(userId, user2Id);
    }

    public List<Film> getRecommendations(Integer userId) {
        findUserById(userId);
        List<Film> films = filmStorage.getRecommendations(userId);
        for (Film film : films) {
            film.setGenres(genreStorage.getGenreListById(film.getId()));
            film.setLikes(likeStorage.getLikesById(film.getId()));
            film.setDirectors(directorStorage.getDirectorsListById(film.getId()));
        }
        log.info("Список рекомендаций пользователя с id {}", userId);
        return films;
    }
}
