package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    /**
     * Returns a list of all users
     * @return list of all users
     */
    List<User> getUsers();

    /**
     * Returns a user by id
     * @param id
     * @return user or null if there was no one
     */
    Optional<User> getUserById(Long id);

    /**
     * Creates a new user
     * @param user
     * @return new user
     */
    User createUser(User user);

    /**
     * Updates the user
     * @param user
     * @return updated user
     */
    User updateUser(User user);
}