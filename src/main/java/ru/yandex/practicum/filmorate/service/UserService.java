package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService {

    private final Map<Integer, User> users = new HashMap<>();

    /**
     * Returns a list of all users
     * @return list of all users
     */
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    /**
     * Creates a new user
     * @param user
     * @return new user
     */
    public User create(User user) {
        return null;
    }

    /**
     * Updates the user
     * @param user
     * @return updated user
     */
    public User update(User user) {
        return null;
    }
}