package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService {

    private int nextId = 0;
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
    public User createUser(@Valid User user) {
        if (user.getId() != 0) {
            throw new ValidationException("The user must have an empty ID when created");
        }

        changeNameToLogin(user);
        user.setId(generateId());
        users.put(user.getId(), user);

        return user;
    }

    /**
     * Updates the user
     * @param user
     * @return updated user
     */
    public User updateUser(@Valid User user) {
        if (user.getId() == 0) {
            throw new ValidationException("The user must not have an empty ID when updating");
        }

        if (!users.containsKey(user.getId())) {
            throw new ValidationException("This user does not exist");
        }

        changeNameToLogin(user);
        users.put(user.getId(), user);

        return user;
    }

    private int generateId() {
        return ++nextId;
    }

    private void changeNameToLogin(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}