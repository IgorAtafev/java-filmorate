package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {

    /**
     * Returns a list of all users
     * @return list of all users
     */
    List<User> getUsers();

    /**
     * Returns user by id.
     * If the user is not found throws NotFoundException
     * @param id
     * @return user by id
     */
    User getUserById(Long id);

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
     * If the user or friend is not found throws NotFoundException
     * @param id
     * @param friendId
     */
    void addFriend(Long id, Long friendId);

    /**
     * Removes a user as a friend
     * If the user or friend is not found throws NotFoundException
     * @param id
     * @param friendId
     */
    void removeFriend(Long id, Long friendId);

    /**
     * Returns a list of the user's friends
     * If the user or friend is not found throws NotFoundException
     * @param id
     * @return list of friends of the user
     */
    List<User> getFriends(Long id);

    /**
     * Returns a list of common friends of users
     * If the user or other user is not found throws NotFoundException
     * @param id
     * @param otherId
     * @return list of common friends of the user
     */
    List<User> getCommonFriends(Long id, Long otherId);
}