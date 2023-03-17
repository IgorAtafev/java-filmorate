package ru.yandex.practicum.filmorate.storage;

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

    /**
     * Adds a user as a friend
     * @param id
     * @param friendId
     */
    void addFriend(Long id, Long friendId);

    /**
     * Removes a user as a friend
     * @param id
     * @param friendId
     */
    void removeFriend(Long id, Long friendId);

    /**
     * Returns a list of the user's friends
     * @param id
     * @return list of friends of the user
     */
    List<User> getFriends(Long id);

    /**
     * Returns a list of common friends of users
     * @param id
     * @param otherId
     * @return list of common friends of the user
     */
    List<User> getCommonFriends(Long id, Long otherId);

    /**
     * Checks for the existence of User by id
     * @param id
     * @return true or false
     */
    boolean isUserExists(Long id);
}